package com.backend.allreva.module.concert.place.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.module.concert.place.application.dto.ConcertHallDetailResponse;
import com.backend.allreva.module.concert.place.application.dto.RelatedConcertResponse;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import com.backend.allreva.module.concert.place.domain.value.ConvenienceInfo;
import com.backend.allreva.module.concert.place.domain.value.Location;
import com.backend.allreva.module.concert.place.exception.ConcertHallErrorCode;
import com.backend.allreva.module.concert.place.fixture.ConcertHallFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("HallService 단위 테스트")
class HallServiceTest {

    @InjectMocks
    private HallService hallService;

    @Mock
    private ConcertHallRepository concertHallRepository;

    @Mock
    private ConcertRepository concertRepository;

    @Nested
    @DisplayName("공연장 상세 조회")
    class Describe_공연장_상세_조회 {

        @Nested
        @DisplayName("공연장 코드로 조회할 때")
        class Context_공연장_코드로_조회 {

            @Test
            @DisplayName("공연장 상세 정보가 반환된다")
            void 공연장_상세_정보가_반환된다() {
                // given
                String hallCode = "FC001";
                ConcertHallDetailResponse expectedResponse = new ConcertHallDetailResponse(
                        "서울 예술의전당",
                        2500,
                        4.5,
                        ConvenienceInfo.builder().build(),
                        Location.builder().build()
                );

                given(concertHallRepository.findDetailByHallCode(hallCode)).willReturn(expectedResponse);

                // when
                ConcertHallDetailResponse result = hallService.findDetailByHallCode(hallCode);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result).isNotNull();
                    softly.assertThat(result.name()).isEqualTo("서울 예술의전당");
                    softly.assertThat(result.seatScale()).isEqualTo(2500);
                });
                verify(concertHallRepository, times(1)).findDetailByHallCode(hallCode);
            }
        }
    }

    @Nested
    @DisplayName("연관 공연 조회")
    class Describe_연관_공연_조회 {

        @Nested
        @DisplayName("공연장 코드로 연관 공연을 조회할 때")
        class Context_연관_공연_조회 {

            @Test
            @DisplayName("해당 공연장에서 진행되는 공연 목록이 반환된다")
            void 해당_공연장의_공연_목록이_반환된다() {
                // given
                String hallCode = "FC001";
                Long lastId = 0L;
                Long lastViewCount = 0L;
                int pageSize = 10;

                List<RelatedConcertResponse> expectedConcerts = List.of(
                        new RelatedConcertResponse(
                                1L,
                                "아이유 콘서트",
                                LocalDate.of(2030, 12, 1),
                                LocalDate.of(2030, 12, 31),
                                "poster1.jpg",
                                100L
                        )
                );

                given(concertRepository.findRelatedConcertsByHall(
                        anyString(), anyLong(), anyLong(), anyInt()))
                        .willReturn(expectedConcerts);

                // when
                List<RelatedConcertResponse> result = hallService.getRelatedConcert(
                        hallCode, lastId, lastViewCount, pageSize);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result).hasSize(1);
                    softly.assertThat(result.get(0).title()).isEqualTo("아이유 콘서트");
                    softly.assertThat(result.get(0).viewCount()).isEqualTo(100L);
                });
                verify(concertRepository, times(1))
                        .findRelatedConcertsByHall(hallCode, lastId, lastViewCount, pageSize);
            }

            @Test
            @DisplayName("조회 중 예외가 발생하면 CustomException이 발생한다")
            void 조회_중_예외가_발생하면_CustomException이_발생한다() {
                // given
                String hallCode = "FC001";
                given(concertRepository.findRelatedConcertsByHall(
                        anyString(), anyLong(), anyLong(), anyInt()))
                        .willThrow(new RuntimeException("Database error"));

                // when & then
                assertThatThrownBy(() ->
                        hallService.getRelatedConcert(hallCode, 0L, 0L, 10))
                        .isInstanceOf(CustomException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ConcertHallErrorCode.RELATED_CONCERT_EXCEPTION);
            }
        }
    }

    @Nested
    @DisplayName("공연장 별점 업데이트")
    class Describe_공연장_별점_업데이트 {

        @Nested
        @DisplayName("리뷰 추가 시 별점을 업데이트할 때")
        class Context_리뷰_추가로_별점_업데이트 {

            @Test
            @DisplayName("공연장의 별점이 정상적으로 업데이트된다")
            void 공연장의_별점이_정상적으로_업데이트된다() {
                // given
                String hallId = "FC001";
                ConcertHall concertHall = ConcertHallFixture.createConcertHall(hallId);
                int starDelta = 5; // 5점 리뷰 추가
                int countDelta = 1; // 리뷰 1개 추가

                given(concertHallRepository.findByIdWithLock(hallId)).willReturn(Optional.of(concertHall));
                given(concertHallRepository.save(any(ConcertHall.class))).willReturn(concertHall);

                // when
                ConcertHall result = hallService.updateConcertHallStar(hallId, starDelta, countDelta);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result).isNotNull();
                    softly.assertThat(result.getId()).isEqualTo(hallId);
                    softly.assertThat(result.getReviewCount()).isEqualTo(1);
                });
                verify(concertHallRepository, times(1)).findByIdWithLock(hallId);
                verify(concertHallRepository, times(1)).save(concertHall);
            }

            @Test
            @DisplayName("존재하지 않는 공연장이면 CustomException이 발생한다")
            void 존재하지_않는_공연장이면_CustomException이_발생한다() {
                // given
                String hallId = "INVALID";
                given(concertHallRepository.findByIdWithLock(hallId)).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() ->
                        hallService.updateConcertHallStar(hallId, 5, 1))
                        .isInstanceOf(CustomException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ConcertHallErrorCode.CONCERT_HALL_SEARCH_NOTFOUND);
            }
        }

        @Nested
        @DisplayName("리뷰 수정 시 별점을 업데이트할 때")
        class Context_리뷰_수정으로_별점_업데이트 {

            @Test
            @DisplayName("별점 차이만큼 공연장 별점이 업데이트된다")
            void 별점_차이만큼_공연장_별점이_업데이트된다() {
                // given
                String hallId = "FC001";
                ConcertHall concertHall = ConcertHallFixture.createConcertHallWithStar(hallId, 4.0, 10);
                int starDelta = 1; // 4점 -> 5점으로 변경 (차이 +1)
                int countDelta = 0; // 리뷰 개수는 변경 없음

                given(concertHallRepository.findByIdWithLock(hallId)).willReturn(Optional.of(concertHall));
                given(concertHallRepository.save(any(ConcertHall.class))).willReturn(concertHall);

                // when
                ConcertHall result = hallService.updateConcertHallStar(hallId, starDelta, countDelta);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result).isNotNull();
                    softly.assertThat(result.getReviewCount()).isEqualTo(10); // 리뷰 개수 유지
                });
                verify(concertHallRepository, times(1)).save(concertHall);
            }
        }

        @Nested
        @DisplayName("리뷰 삭제 시 별점을 업데이트할 때")
        class Context_리뷰_삭제로_별점_업데이트 {

            @Test
            @DisplayName("삭제된 리뷰만큼 별점과 개수가 감소한다")
            void 삭제된_리뷰만큼_별점과_개수가_감소한다() {
                // given
                String hallId = "FC001";
                ConcertHall concertHall = ConcertHallFixture.createConcertHallWithStar(hallId, 4.5, 10);
                int starDelta = -5; // 5점 리뷰 삭제
                int countDelta = -1; // 리뷰 1개 감소

                given(concertHallRepository.findByIdWithLock(hallId)).willReturn(Optional.of(concertHall));
                given(concertHallRepository.save(any(ConcertHall.class))).willReturn(concertHall);

                // when
                ConcertHall result = hallService.updateConcertHallStar(hallId, starDelta, countDelta);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result).isNotNull();
                    softly.assertThat(result.getReviewCount()).isEqualTo(9); // 10 - 1
                });
                verify(concertHallRepository, times(1)).save(concertHall);
            }
        }
    }
}
