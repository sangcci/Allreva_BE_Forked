package com.backend.allreva.module.concert.concert.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.backend.allreva.module.concert.concert.application.dto.ConcertDetailResponse;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
            @DisplayName("공연 상세 정보가 반환된다")
            void 공연_상세_정보가_반환된다() {
                // given
                Long concertId = 1L;
                ConcertDetailResponse expected = ConcertDetailResponse.EMPTY;
                given(concertRepository.findDetailById(concertId)).willReturn(expected);

                // when
                ConcertDetailResponse result = concertService.findDetailById(concertId);

                // then
                assertThat(result).isNotNull();
                verify(concertRepository, times(1)).findDetailById(concertId);
            }
        }
    }
}
