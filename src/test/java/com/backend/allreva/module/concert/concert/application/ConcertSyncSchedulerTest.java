package com.backend.allreva.module.concert.concert.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.backend.allreva.module.concert.concert.application.port.ConcertDataSyncPort;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.module.concert.concert.fixture.ConcertFixture;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("ConcertSyncScheduler 단위 테스트")
class ConcertSyncSchedulerTest {

    @InjectMocks
    private ConcertSyncScheduler scheduler;

    @Mock
    private ConcertDataSyncPort concertDataSyncPort;

    @Mock
    private ConcertRepository concertRepository;

    @Mock
    private ConcertHallRepository concertHallRepository;

    @Nested
    @DisplayName("공연 정보 동기화")
    class Describe_공연_정보_동기화 {

        @Nested
        @DisplayName("DB에 없는 새 공연 코드일 때")
        class Context_신규_공연일_때 {

            @Test
            @DisplayName("공연 정보를 저장한다")
            void 신규_공연_정보를_저장한다() {
                // given
                String hallId = "FC001114-1";
                String concertCode = "PF001";
                Concert newConcert = ConcertFixture.createInProgressConcert(concertCode);

                given(concertHallRepository.findAllIds()).willReturn(List.of(hallId));
                given(concertDataSyncPort.fetchDailyConcertCodes(anyString(), anyString(), anyString(), anyString()))
                        .willReturn(List.of(concertCode));
                given(concertDataSyncPort.fetchConcertDetail(hallId, concertCode))
                        .willReturn(newConcert);
                given(concertRepository.findByCodeConcertCode(concertCode)).willReturn(Optional.empty());

                // when
                scheduler.fetchDailyConcertInfoList("20260421");

                // then
                verify(concertRepository, times(1)).save(newConcert);
            }
        }

        @Nested
        @DisplayName("기존 공연이 COMPLETED 상태일 때")
        class Context_COMPLETED_공연일_때 {

            @Test
            @DisplayName("공연 정보를 업데이트하지 않는다")
            void COMPLETED_공연은_업데이트하지_않는다() {
                // given
                String hallId = "FC001114-1";
                String concertCode = "PF002";
                Concert completedConcert = ConcertFixture.createCompletedConcert(concertCode);
                Concert newData = ConcertFixture.createCompletedConcert(concertCode);

                given(concertHallRepository.findAllIds()).willReturn(List.of(hallId));
                given(concertDataSyncPort.fetchDailyConcertCodes(anyString(), anyString(), anyString(), anyString()))
                        .willReturn(List.of(concertCode));
                given(concertDataSyncPort.fetchConcertDetail(hallId, concertCode))
                        .willReturn(newData);
                given(concertRepository.findByCodeConcertCode(concertCode)).willReturn(Optional.of(completedConcert));

                // when
                scheduler.fetchDailyConcertInfoList("20260421");

                // then
                verify(concertRepository, never()).save(any());
            }
        }

        @Nested
        @DisplayName("기존 공연이 IN_PROGRESS 상태일 때")
        class Context_IN_PROGRESS_공연일_때 {

            @Test
            @DisplayName("공연 정보를 업데이트한다")
            void IN_PROGRESS_공연은_업데이트한다() {
                // given
                String hallId = "FC001114-1";
                String concertCode = "PF003";
                Concert existingConcert = ConcertFixture.createInProgressConcert(concertCode);
                Concert newData = ConcertFixture.createInProgressConcert(concertCode);

                given(concertHallRepository.findAllIds()).willReturn(List.of(hallId));
                given(concertDataSyncPort.fetchDailyConcertCodes(anyString(), anyString(), anyString(), anyString()))
                        .willReturn(List.of(concertCode));
                given(concertDataSyncPort.fetchConcertDetail(hallId, concertCode))
                        .willReturn(newData);
                given(concertRepository.findByCodeConcertCode(concertCode)).willReturn(Optional.of(existingConcert));

                // when
                scheduler.fetchDailyConcertInfoList("20260421");

                // then
                verify(concertRepository, times(1)).save(existingConcert);
            }
        }
    }
}
