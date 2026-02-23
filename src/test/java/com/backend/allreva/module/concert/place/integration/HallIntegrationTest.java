package com.backend.allreva.module.concert.place.integration;

import static com.backend.allreva.module.concert.place.fixture.ConcertHallFixture.createTestConcertHall;

import com.backend.allreva.module.concert.place.application.HallService;
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
@DisplayName("Hall 통합 테스트")
class HallIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private HallService hallService;

    @Autowired
    private ConcertHallRepository concertHallRepository;

    @AfterEach
    void tearDown() {
        concertHallRepository.deleteAll();
    }

    @Nested
    @DisplayName("공연장 별점 업데이트")
    class Describe_공연장_별점_업데이트 {

        @Nested
        @DisplayName("리뷰 추가 시")
        class Context_리뷰_추가 {

            @Test
            @DisplayName("공연장의 별점과 리뷰 개수가 정상적으로 업데이트된다")
            void 공연장의_별점과_리뷰_개수가_업데이트된다() {
                // given
                ConcertHall hall = concertHallRepository.save(createTestConcertHall());
                String hallId = hall.getId();
                long initialReviewCount = hall.getReviewCount();
                double initialStar = hall.getStar();

                int newReviewStar = 5;

                // when
                ConcertHall result = hallService.updateConcertHallStar(hallId, newReviewStar, 1);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result.getReviewCount()).isEqualTo(initialReviewCount + 1);
                    // 별점 계산: (기존 총점 + 새 리뷰 별점) / 새 리뷰 개수
                    double expectedStar = (initialStar * initialReviewCount + newReviewStar) / (initialReviewCount + 1);
                    softly.assertThat(result.getStar()).isGreaterThan(initialStar);
                });
            }

            @Test
            @DisplayName("여러 리뷰를 추가하면 평균 별점이 계산된다")
            void 여러_리뷰_추가시_평균_별점이_계산된다() {
                // given
                ConcertHall hall = concertHallRepository.save(createTestConcertHall());
                String hallId = hall.getId();

                // when - 5점, 4점, 3점 리뷰 추가
                hallService.updateConcertHallStar(hallId, 5, 1);
                hallService.updateConcertHallStar(hallId, 4, 1);
                ConcertHall result = hallService.updateConcertHallStar(hallId, 3, 1);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result.getReviewCount()).isEqualTo(3);
                    softly.assertThat(result.getStar()).isEqualTo(4.0); // (5 + 4 + 3) / 3 = 4.0
                });
            }
        }

        @Nested
        @DisplayName("리뷰 수정 시")
        class Context_리뷰_수정 {

            @Test
            @DisplayName("별점 차이만큼 공연장 별점이 업데이트된다")
            void 별점_차이만큼_공연장_별점이_업데이트된다() {
                // given
                ConcertHall hall = concertHallRepository.save(createTestConcertHall());
                String hallId = hall.getId();

                // 초기 리뷰 2개 추가 (5점, 3점) -> 평균 4.0
                hallService.updateConcertHallStar(hallId, 5, 1);
                hallService.updateConcertHallStar(hallId, 3, 1);

                // when - 3점 리뷰를 5점으로 수정 (차이 +2)
                ConcertHall result = hallService.updateConcertHallStar(hallId, 2, 0);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result.getReviewCount()).isEqualTo(2); // 개수는 그대로
                    softly.assertThat(result.getStar()).isEqualTo(5.0); // (5 + 5) / 2 = 5.0
                });
            }
        }

        @Nested
        @DisplayName("리뷰 삭제 시")
        class Context_리뷰_삭제 {

            @Test
            @DisplayName("삭제된 리뷰만큼 별점과 개수가 감소한다")
            void 삭제된_리뷰만큼_별점과_개수가_감소한다() {
                // given
                ConcertHall hall = concertHallRepository.save(createTestConcertHall());
                String hallId = hall.getId();

                // 초기 리뷰 3개 추가 (5점, 4점, 3점) -> 평균 4.0
                hallService.updateConcertHallStar(hallId, 5, 1);
                hallService.updateConcertHallStar(hallId, 4, 1);
                hallService.updateConcertHallStar(hallId, 3, 1);

                // when - 3점 리뷰 삭제
                ConcertHall result = hallService.updateConcertHallStar(hallId, -3, -1);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result.getReviewCount()).isEqualTo(2);
                    softly.assertThat(result.getStar()).isEqualTo(4.5); // (5 + 4) / 2 = 4.5
                });
            }

            @Test
            @DisplayName("마지막 리뷰 삭제 시 별점이 0이 된다")
            void 마지막_리뷰_삭제시_별점이_0이_된다() {
                // given
                ConcertHall hall = concertHallRepository.save(createTestConcertHall());
                String hallId = hall.getId();

                // 리뷰 1개 추가 (5점)
                hallService.updateConcertHallStar(hallId, 5, 1);

                // when - 유일한 리뷰 삭제
                ConcertHall result = hallService.updateConcertHallStar(hallId, -5, -1);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result.getReviewCount()).isEqualTo(0);
                    softly.assertThat(result.getStar()).isEqualTo(0.0);
                });
            }
        }
    }

    @Nested
    @DisplayName("동시성 제어")
    class Describe_동시성_제어 {

        @Nested
        @DisplayName("여러 리뷰가 동시에 추가될 때")
        class Context_동시_리뷰_추가 {

            @Test
            @DisplayName("비관적 락으로 데이터 일관성이 유지된다")
            void 비관적_락으로_데이터_일관성이_유지된다() throws InterruptedException {
                // given
                ConcertHall hall = concertHallRepository.save(createTestConcertHall());
                String hallId = hall.getId();

                // when - 동시에 5점 리뷰 10개 추가 시도
                Thread[] threads = new Thread[10];
                for (int i = 0; i < 10; i++) {
                    threads[i] = new Thread(() ->
                            hallService.updateConcertHallStar(hallId, 5, 1));
                    threads[i].start();
                }

                for (Thread thread : threads) {
                    thread.join();
                }

                // then
                ConcertHall result = concertHallRepository.findById(hallId).orElseThrow();
                assertSoftly(softly -> {
                    softly.assertThat(result.getReviewCount()).isEqualTo(10);
                    softly.assertThat(result.getStar()).isEqualTo(5.0);
                });
            }
        }
    }
}
