package com.backend.allreva.module.search.integration;

import static com.backend.allreva.module.concert.hall.fixture.ConcertHallFixture.createConcertHall;
import static com.backend.allreva.module.concert.hall.fixture.ConcertHallFixture.createTestConcertHall;

import com.backend.allreva.module.concert.hall.application.dto.ConcertHallMainResponse;
import com.backend.allreva.module.concert.hall.domain.ConcertHallRepository;
import com.backend.allreva.module.search.application.ConcertHallSearchService;
import com.backend.allreva.support.IntegrationTestSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Slf4j
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("ConcertHall 검색 통합 테스트")
class ConcertHallSearchServiceTest extends IntegrationTestSupport {

    @Autowired
    ConcertHallSearchService concertHallSearchService;

    @Autowired
    ConcertHallRepository concertHallRepository;

    @AfterEach
    void tearDown() {
        concertHallRepository.deleteAll();
    }

    @Nested
    @DisplayName("메인 공연장 검색")
    class Describe_메인_공연장_검색 {

        @Nested
        @DisplayName("지역으로 검색할 때")
        class Context_지역으로_검색 {

            @Test
            @DisplayName("해당 지역의 공연장이 반환된다")
            void 해당_지역의_공연장이_반환된다() {
                // given
                concertHallRepository.save(createTestConcertHall());
                String address = "서울";
                int seatScale = 0;
                int size = 10;

                // when
                ConcertHallMainResponse result = concertHallSearchService.searchMainConcertHalls(
                        address, seatScale, null, size);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result).isNotNull();
                    softly.assertThat(result.concertHallThumbnails()).isNotEmpty();
                });
            }
        }

        @Nested
        @DisplayName("좌석 규모로 필터링할 때")
        class Context_좌석_규모로_필터링 {

            @Test
            @DisplayName("지정한 좌석 규모 이상의 공연장만 반환된다")
            void 지정한_좌석_규모_이상의_공연장만_반환된다() {
                // given
                concertHallRepository.save(createTestConcertHall());
                String address = "";
                int seatScale = 2000;
                int size = 10;

                // when
                ConcertHallMainResponse result = concertHallSearchService.searchMainConcertHalls(
                        address, seatScale, null, size);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result).isNotNull();
                    softly.assertThat(result.concertHallThumbnails()).isNotEmpty();
                    softly.assertThat(result.concertHallThumbnails())
                            .allMatch(hall -> hall.seatScale() >= seatScale);
                });
            }
        }

        @Nested
        @DisplayName("페이지네이션을 사용한 조회 시")
        class Context_페이지네이션_조회 {

            @Test
            @DisplayName("nextCursorId를 사용하여 다음 페이지를 조회할 수 있다")
            void nextCursorId를_사용하여_다음_페이지를_조회할_수_있다() {
                // given - 각각 다른 ID로 5개 저장
                for (int i = 1; i <= 5; i++) {
                    concertHallRepository.save(createConcertHall(String.format("hall-%03d", i)));
                }
                String address = "";
                int seatScale = 0;
                int pageSize = 2;

                // when - 첫 페이지 조회
                ConcertHallMainResponse page1 = concertHallSearchService.searchMainConcertHalls(
                        address, seatScale, null, pageSize);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(page1.concertHallThumbnails()).hasSize(2);
                    softly.assertThat(page1.nextCursorId()).isNotNull();
                });

                // when - 두 번째 페이지 조회
                ConcertHallMainResponse page2 = concertHallSearchService.searchMainConcertHalls(
                        address, seatScale, page1.nextCursorId(), pageSize);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(page2.concertHallThumbnails()).hasSize(2);
                    softly.assertThat(page2.concertHallThumbnails())
                            .isNotEqualTo(page1.concertHallThumbnails());
                });
            }
        }
    }
}
