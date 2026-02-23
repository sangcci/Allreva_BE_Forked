package com.backend.allreva.module.concert.concert.application;

import com.backend.allreva.module.concert.concert.application.dto.ConcertDetailResponse;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.fixture.ConcertFixture;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("ConcertService 단위 테스트")
class ConcertServiceTest {

    @InjectMocks
    private ConcertService concertService;

    @Mock
    private ConcertRepository concertRepository;

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
                Long concertId = 1L;
                Concert concert = ConcertFixture.createConcert(concertId);
                long initialViewCount = concert.getViewCount();

                ConcertDetailResponse expectedResponse = ConcertDetailResponse.EMPTY;

                given(concertRepository.findById(concertId)).willReturn(Optional.of(concert));
                given(concertRepository.findDetailById(concertId)).willReturn(expectedResponse);

                // when
                ConcertDetailResponse result = concertService.findDetailById(concertId);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result).isNotNull();
                    softly.assertThat(concert.getViewCount()).isEqualTo(initialViewCount + 1);
                });
                verify(concertRepository, times(1)).findById(concertId);
                verify(concertRepository, times(1)).findDetailById(concertId);
            }

            @Test
            @DisplayName("존재하지 않는 공연 ID로 조회하면 조회수 증가 없이 조회만 수행된다")
            void 존재하지_않는_공연은_조회수_증가_없이_조회만_수행된다() {
                // given
                Long concertId = 999L;
                ConcertDetailResponse expectedResponse = ConcertDetailResponse.EMPTY;

                given(concertRepository.findById(concertId)).willReturn(Optional.empty());
                given(concertRepository.findDetailById(concertId)).willReturn(expectedResponse);

                // when
                ConcertDetailResponse result = concertService.findDetailById(concertId);

                // then
                assertThat(result).isNotNull();
                verify(concertRepository, times(1)).findById(concertId);
                verify(concertRepository, times(1)).findDetailById(concertId);
            }
        }
    }

    @Nested
    @DisplayName("조회수 증가")
    class Describe_조회수_증가 {

        @Nested
        @DisplayName("공연 ID로 조회수를 증가시킬 때")
        class Context_조회수_증가 {

            @Test
            @DisplayName("공연의 조회수가 1 증가한다")
            void 공연의_조회수가_1_증가한다() {
                // given
                Long concertId = 1L;
                Concert concert = ConcertFixture.createConcert(concertId);
                long initialViewCount = concert.getViewCount();

                given(concertRepository.findById(concertId)).willReturn(Optional.of(concert));

                // when
                concertService.increaseViewCount(concertId);

                // then
                assertThat(concert.getViewCount()).isEqualTo(initialViewCount + 1);
                verify(concertRepository, times(1)).findById(concertId);
            }

            @Test
            @DisplayName("존재하지 않는 공연은 조회수가 증가하지 않는다")
            void 존재하지_않는_공연은_조회수가_증가하지_않는다() {
                // given
                Long concertId = 999L;
                given(concertRepository.findById(concertId)).willReturn(Optional.empty());

                // when
                concertService.increaseViewCount(concertId);

                // then
                verify(concertRepository, times(1)).findById(concertId);
            }
        }
    }
}
