package com.backend.allreva.module.concert.concert.integration;

import static com.backend.allreva.module.concert.place.fixture.ConcertHallFixture.createTestConcertHall;

import static com.backend.allreva.module.concert.concert.fixture.ConcertFixture.createTestConcert;
import static com.backend.allreva.module.concert.concert.fixture.ConcertFixture.createConcertWithHallCode;

import com.backend.allreva.module.concert.concert.application.ConcertService;
import com.backend.allreva.module.concert.concert.application.dto.ConcertDetailResponse;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.ConcertInfo;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import com.backend.allreva.support.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("Concert 통합 테스트")
class ConcertIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private ConcertService concertService;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertHallRepository concertHallRepository;

    @AfterEach
    void tearDown() {
        concertRepository.deleteAll();
        concertHallRepository.deleteAll();
    }

    @Nested
    @DisplayName("공연 상세 조회")
    class Describe_공연_상세_조회 {

        @Nested
        @DisplayName("공연 ID로 조회할 때")
        class Context_공연_ID로_조회 {

            @Test
            @DisplayName("조회수가 1 증가하고 상세 정보가 반환된다")
            void 조회수가_증가하고_상세정보가_반환된다() {
                // given
                ConcertHall hall = concertHallRepository.save(createTestConcertHall());
                Concert concert = concertRepository.save(createConcertWithHallCode(hall.getId()));
                Long concertId = concert.getId();
                long initialViewCount = concert.getViewCount();

                // when
                ConcertDetailResponse result = concertService.findDetailById(concertId);

                // then
                Concert updatedConcert = concertRepository.findById(concertId).orElseThrow();
                assertSoftly(softly -> {
                    softly.assertThat(result).isNotNull();
                    softly.assertThat(result.hallCode()).isNotNull();
                    softly.assertThat(updatedConcert.getViewCount()).isEqualTo(initialViewCount + 1);
                });
            }
        }

        @Nested
        @DisplayName("조회수를 증가시킬 때")
        class Context_조회수_증가 {

            @Test
            @DisplayName("공연의 조회수가 1 증가한다")
            void 공연의_조회수가_1_증가한다() {
                // given
                Concert concert = concertRepository.save(createTestConcert());
                Long concertId = concert.getId();
                long initialViewCount = concert.getViewCount();

                // when
                concertService.increaseViewCount(concertId);

                // then
                Concert updatedConcert = concertRepository.findById(concertId).orElseThrow();
                assertThat(updatedConcert.getViewCount()).isEqualTo(initialViewCount + 1);
            }

            @Test
            @DisplayName("여러 번 조회하면 조회수가 누적된다")
            void 여러_번_조회하면_조회수가_누적된다() {
                // given
                Concert concert = concertRepository.save(createTestConcert());
                Long concertId = concert.getId();
                long initialViewCount = concert.getViewCount();

                // when
                concertService.increaseViewCount(concertId);
                concertService.increaseViewCount(concertId);
                concertService.increaseViewCount(concertId);

                // then
                Concert updatedConcert = concertRepository.findById(concertId).orElseThrow();
                assertThat(updatedConcert.getViewCount()).isEqualTo(initialViewCount + 3);
            }
        }
    }

    @Nested
    @DisplayName("공연 정보 업데이트")
    class Describe_공연_정보_업데이트 {

        @Nested
        @DisplayName("공연 정보를 수정할 때")
        class Context_공연_정보_수정 {

            @Test
            @DisplayName("공연 정보가 정상적으로 업데이트된다")
            void 공연_정보가_정상적으로_업데이트된다() {
                // given
                Concert concert = concertRepository.save(createTestConcert());
                String newTitle = "변경된 콘서트 제목";

                // when
                Concert updatedConcert = concertRepository.findById(concert.getId()).orElseThrow();
                ConcertInfo newConcertInfo = ConcertInfo.builder()
                        .title(newTitle)
                        .price(updatedConcert.getConcertInfo().getPrice())
                        .performStatus(updatedConcert.getConcertInfo().getPerformStatus())
                        .host(updatedConcert.getConcertInfo().getHost())
                        .dateInfo(updatedConcert.getConcertInfo().getDateInfo())
                        .build();

                updatedConcert.updateFrom(
                        updatedConcert.getCode(),
                        newConcertInfo,
                        updatedConcert.getEpisodes(),
                        updatedConcert.getPoster(),
                        updatedConcert.getDetailImages(),
                        updatedConcert.getSellers()
                );
                concertRepository.save(updatedConcert);

                // then
                Concert result = concertRepository.findById(concert.getId()).orElseThrow();
                assertThat(result.getConcertInfo().getTitle()).isEqualTo(newTitle);
            }
        }
    }

    @Nested
    @DisplayName("공연 상세 조회와 공연장 정보")
    class Describe_공연_상세_조회와_공연장_정보 {

        @Nested
        @DisplayName("공연 ID로 상세 조회할 때")
        class Context_공연과_공연장_조회 {

            @Test
            @DisplayName("공연 상세 정보와 해당 공연장 정보가 함께 반환된다")
            void 공연_상세_정보와_공연장_정보가_반환된다() {
                // given
                ConcertHall hall = concertHallRepository.save(createTestConcertHall());
                Concert concert = concertRepository.save(createConcertWithHallCode(hall.getId()));

                // when
                ConcertDetailResponse response = concertService.findDetailById(concert.getId());

                // then
                assertSoftly(softly -> {
                    softly.assertThat(response).isNotNull();
                    softly.assertThat(response.hallCode()).isNotNull();
                    softly.assertThat(response.hallName()).isNotNull();
                    softly.assertThat(response.sellers()).isNotEmpty();
                    softly.assertThat(response.convenienceInfo()).isNotNull();
                });
            }
        }
    }
}
