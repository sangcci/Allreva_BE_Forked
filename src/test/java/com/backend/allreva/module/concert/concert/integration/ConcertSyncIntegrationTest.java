package com.backend.allreva.module.concert.concert.integration;

import static com.backend.allreva.module.concert.concert.fixture.ConcertFixture.createCompletedConcert;
import static com.backend.allreva.module.concert.concert.fixture.ConcertFixture.createInProgressConcert;
import static com.backend.allreva.module.concert.concert.fixture.ConcertFixture.createInProgressConcertWithTitle;
import static com.backend.allreva.module.concert.concert.fixture.ConcertFixture.createScheduledConcert;
import static com.backend.allreva.module.concert.place.fixture.ConcertHallFixture.createConcertHall;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.backend.allreva.module.concert.concert.application.ConcertSyncScheduler;
import com.backend.allreva.module.concert.concert.application.dto.ConcertSummary;
import com.backend.allreva.module.concert.concert.application.port.ConcertDataSyncPort;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.module.concert.concert.domain.value.ConcertStatus;
import com.backend.allreva.module.concert.place.application.ConcertHallSyncScheduler;
import com.backend.allreva.module.concert.place.application.port.ConcertHallDataSyncPort;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import com.backend.allreva.module.concert.place.domain.value.ConvenienceInfo;
import com.backend.allreva.module.concert.place.domain.value.Location;
import com.backend.allreva.support.IntegrationTestSupport;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("Concert Sync 통합 테스트")
class ConcertSyncIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private ConcertSyncScheduler concertSyncScheduler;

    @Autowired
    private ConcertHallSyncScheduler concertHallSyncScheduler;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertHallRepository concertHallRepository;

    @MockBean
    private ConcertDataSyncPort concertDataSyncPort;

    @MockBean
    private ConcertHallDataSyncPort concertHallDataSyncPort;

    @AfterEach
    void tearDown() {
        concertRepository.deleteAll();
        concertHallRepository.deleteAll();
    }

    @Nested
    @DisplayName("공연 정보 동기화")
    class Describe_공연_정보_동기화 {

        @Nested
        @DisplayName("DB에 없는 신규 공연이 API에서 조회될 때")
        class Context_신규_공연 {

            @Test
            @DisplayName("공연 정보를 DB에 저장한다")
            void savesNewConcertToDb() {
                // given
                String hallId = "FC001114-1";
                String concertCode = "PF001";
                concertHallRepository.save(createConcertHall(hallId));

                given(concertDataSyncPort.fetchDailyConcertSummaries(
                                anyString(), anyString(), anyString(), anyString()))
                        .willReturn(List.of(new ConcertSummary(concertCode, ConcertStatus.IN_PROGRESS)));
                given(concertDataSyncPort.fetchConcertDetail(hallId, concertCode))
                        .willReturn(createInProgressConcert(concertCode));

                // when
                concertSyncScheduler.fetchDailyConcertInfoList("20260424");

                // then
                Concert saved = concertRepository.findById(concertCode).orElseThrow();
                assertSoftly(softly -> {
                    softly.assertThat(saved.getConcertInfo().getTitle()).isEqualTo("진행 중인 공연");
                    softly.assertThat(saved.getConcertCode()).isEqualTo(concertCode);
                });
            }
        }

        @Nested
        @DisplayName("기존 COMPLETED 공연이 API에서 조회될 때")
        class Context_COMPLETED_공연_스킵 {

            @Test
            @DisplayName("상세를 호출하지 않고 스킵한다")
            void skipsCompletedConcert() {
                // given
                String hallId = "FC001114-1";
                String concertCode = "PF003";
                concertHallRepository.save(createConcertHall(hallId));
                concertRepository.save(createCompletedConcert(concertCode));

                given(concertDataSyncPort.fetchDailyConcertSummaries(
                                anyString(), anyString(), anyString(), anyString()))
                        .willReturn(List.of(new ConcertSummary(concertCode, ConcertStatus.COMPLETED)));

                // when
                concertSyncScheduler.fetchDailyConcertInfoList("20260424");

                // then
                verify(concertDataSyncPort, never()).fetchConcertDetail(anyString(), anyString());
                Concert result = concertRepository.findById(concertCode).orElseThrow();
                assertThat(result.getConcertInfo().getTitle()).isEqualTo("종료된 공연"); // unchanged
            }
        }

        @Nested
        @DisplayName("기존 SCHEDULED 공연이 API에서 IN_PROGRESS로 변경되어 조회될 때")
        class Context_SCHEDULED_에서_IN_PROGRESS로_상태전이 {

            @Test
            @DisplayName("상세를 호출하고 업데이트한다")
            void callsDetailAndUpdatesOnStatusTransition() {
                // given
                String hallId = "FC001114-1";
                String concertCode = "PF004";
                concertHallRepository.save(createConcertHall(hallId));
                concertRepository.save(createScheduledConcert(concertCode));

                given(concertDataSyncPort.fetchDailyConcertSummaries(
                                anyString(), anyString(), anyString(), anyString()))
                        .willReturn(List.of(new ConcertSummary(concertCode, ConcertStatus.IN_PROGRESS)));
                given(concertDataSyncPort.fetchConcertDetail(hallId, concertCode))
                        .willReturn(createInProgressConcertWithTitle(concertCode, "공연중"));

                // when
                concertSyncScheduler.fetchDailyConcertInfoList("20260424");

                // then
                verify(concertDataSyncPort, times(1)).fetchConcertDetail(hallId, concertCode);
                Concert result = concertRepository.findById(concertCode).orElseThrow();
                assertThat(result.getConcertInfo().getTitle()).isEqualTo("공연중");
            }
        }

        @Nested
        @DisplayName("DB에 공연장이 없을 때")
        class Context_공연장_없음 {

            @Test
            @DisplayName("KOPIS API를 호출하지 않는다")
            void doesNotCallApiWhenNoHalls() {
                // given: no halls in DB

                // when
                concertSyncScheduler.fetchDailyConcertInfoList("20260424");

                // then
                verify(concertDataSyncPort, never())
                        .fetchDailyConcertSummaries(anyString(), anyString(), anyString(), anyString());
            }
        }
    }

    @Nested
    @DisplayName("공연장 정보 동기화")
    class Describe_공연장_정보_동기화 {

        @Nested
        @DisplayName("KOPIS API에서 공연장 정보가 조회될 때")
        class Context_공연장_정보_조회 {

            @Test
            @DisplayName("공연장 정보를 DB에 업데이트한다")
            void updatesConcertHallInfoInDb() {
                // given
                String hallId = "FC001114-1";
                concertHallRepository.save(createConcertHall(hallId));

                ConcertHall updatedHall = ConcertHall.builder()
                        .id(hallId)
                        .name("업데이트된 공연장")
                        .seatScale(20000)
                        .convenienceInfo(ConvenienceInfo.builder()
                                .hasParkingLot(false)
                                .hasRestaurant(true)
                                .hasCafe(true)
                                .hasDisabledParking(false)
                                .build())
                        .location(Location.builder()
                                .longitude(127.013079)
                                .latitude(37.518486)
                                .address("서울특별시 송파구 올림픽로 424")
                                .build())
                        .build();
                given(concertHallDataSyncPort.fetchConcertHallDetails("FC001114"))
                        .willReturn(List.of(updatedHall));

                // when
                concertHallSyncScheduler.fetchConcertHallInfoList();

                // then
                ConcertHall result = concertHallRepository.findById(hallId).orElseThrow();
                assertSoftly(softly -> {
                    softly.assertThat(result.getName()).isEqualTo("업데이트된 공연장");
                    softly.assertThat(result.getSeatScale()).isEqualTo(20000);
                });
            }
        }

        @Nested
        @DisplayName("API가 hallId가 다른 공연장도 함께 반환할 때")
        class Context_타_공연장_필터링 {

            @Test
            @DisplayName("hallId가 일치하는 공연장만 저장한다")
            void savesOnlyMatchingHalls() {
                // given
                String hallId = "FC001114-1";
                concertHallRepository.save(createConcertHall(hallId));

                ConcertHall matchingHall = ConcertHall.builder()
                        .id(hallId)
                        .name("KSPO돔")
                        .seatScale(15000)
                        .convenienceInfo(ConvenienceInfo.builder()
                                .hasParkingLot(true)
                                .hasRestaurant(true)
                                .hasCafe(true)
                                .hasDisabledParking(true)
                                .build())
                        .location(Location.builder()
                                .longitude(127.013079)
                                .latitude(37.518486)
                                .address("서울특별시 송파구 올림픽로 424")
                                .build())
                        .build();
                ConcertHall nonMatchingHall = ConcertHall.builder()
                        .id("FC001114-2")
                        .name("별관")
                        .seatScale(500)
                        .convenienceInfo(ConvenienceInfo.builder()
                                .hasParkingLot(false)
                                .hasRestaurant(false)
                                .hasCafe(false)
                                .hasDisabledParking(false)
                                .build())
                        .location(Location.builder()
                                .longitude(127.013079)
                                .latitude(37.518486)
                                .address("서울특별시 송파구 올림픽로 424")
                                .build())
                        .build();
                given(concertHallDataSyncPort.fetchConcertHallDetails("FC001114"))
                        .willReturn(List.of(matchingHall, nonMatchingHall));

                // when
                concertHallSyncScheduler.fetchConcertHallInfoList();

                // then
                assertThat(concertHallRepository.findById("FC001114-2")).isEmpty();
                assertThat(concertHallRepository.findById(hallId)).isPresent();
            }
        }

        @Nested
        @DisplayName("DB에 공연장이 없을 때")
        class Context_공연장_없음 {

            @Test
            @DisplayName("KOPIS API를 호출하지 않는다")
            void doesNotCallApiWhenNoHalls() {
                // given: no halls in DB

                // when
                concertHallSyncScheduler.fetchConcertHallInfoList();

                // then
                verify(concertHallDataSyncPort, never()).fetchConcertHallDetails(anyString());
            }
        }
    }
}
