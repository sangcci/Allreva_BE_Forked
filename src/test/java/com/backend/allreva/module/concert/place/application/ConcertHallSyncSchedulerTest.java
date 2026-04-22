package com.backend.allreva.module.concert.place.application;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.backend.allreva.module.concert.place.application.port.ConcertHallDataSyncPort;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("ConcertHallSyncScheduler 단위 테스트")
class ConcertHallSyncSchedulerTest {

    @InjectMocks
    private ConcertHallSyncScheduler scheduler;

    @Mock
    private ConcertHallDataSyncPort concertHallDataSyncPort;

    @Mock
    private ConcertHallRepository concertHallRepository;

    @Nested
    @DisplayName("공연장 동기화")
    class Describe_공연장_동기화 {

        @Nested
        @DisplayName("DB에 공연장 코드가 있을 때")
        class Context_DB에_공연장_코드가_있을_때 {

            @Test
            @DisplayName("각 공연장 코드로 KOPIS API를 호출하고 저장한다")
            void 각_공연장_코드로_KOPIS_API를_호출하고_저장한다() {
                // given
                List<String> hallIds = List.of("FC001114-1", "FC001032-1");
                List<ConcertHall> halls = List.of(ConcertHall.builder()
                        .id("FC001114-1")
                        .name("KSPO돔")
                        .seatScale(15000)
                        .build());

                given(concertHallRepository.findAllIds()).willReturn(hallIds);
                given(concertHallDataSyncPort.fetchConcertHallDetails("FC001114"))
                        .willReturn(halls);
                given(concertHallDataSyncPort.fetchConcertHallDetails("FC001032"))
                        .willReturn(List.of());

                // when
                scheduler.fetchConcertHallInfoList();

                // then
                verify(concertHallRepository, times(1)).findAllIds();
                verify(concertHallDataSyncPort, times(1)).fetchConcertHallDetails("FC001114");
                verify(concertHallDataSyncPort, times(1)).fetchConcertHallDetails("FC001032");
                verify(concertHallRepository, times(1)).save(halls.get(0));
            }
        }

        @Nested
        @DisplayName("DB에 공연장 코드가 없을 때")
        class Context_DB에_공연장_코드가_없을_때 {

            @Test
            @DisplayName("KOPIS API를 호출하지 않는다")
            void KOPIS_API를_호출하지_않는다() {
                // given
                given(concertHallRepository.findAllIds()).willReturn(List.of());

                // when
                scheduler.fetchConcertHallInfoList();

                // then
                verify(concertHallDataSyncPort, times(0))
                        .fetchConcertHallDetails(org.mockito.ArgumentMatchers.anyString());
            }
        }
    }
}
