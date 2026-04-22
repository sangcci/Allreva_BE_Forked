package com.backend.allreva.module.concert.concert.application;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.backend.allreva.module.concert.concert.application.port.ConcertDataSyncPort;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.fixture.ConcertFixture;
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
@DisplayName("ConcertSyncScheduler 단위 테스트")
class ConcertSyncSchedulerTest {

    @InjectMocks
    private ConcertSyncScheduler scheduler;

    @Mock
    private ConcertDataSyncPort concertDataSyncPort;

    @Mock
    private ConcertHallRepository concertHallRepository;

    @Mock
    private ConcertSyncService concertSyncService;

    @Nested
    @DisplayName("공연 정보 동기화")
    class Describe_공연_정보_동기화 {

        @Nested
        @DisplayName("여러 공演 코드가 조회될 때")
        class Context_여러_공연_코드일_때 {

            @Test
            @DisplayName("각 공연 코드마다 ConcertSyncService.processConcertUpsert를 호출한다")
            void 각_공연마다_processConcertUpsert를_호출한다() {
                // given
                String hallId = "FC001114-1";
                String concertCode1 = "PF001";
                String concertCode2 = "PF002";
                Concert concert1 = ConcertFixture.createInProgressConcert(concertCode1);
                Concert concert2 = ConcertFixture.createInProgressConcert(concertCode2);

                given(concertHallRepository.findAllIds()).willReturn(List.of(hallId));
                given(concertDataSyncPort.fetchDailyConcertCodes(anyString(), anyString(), anyString(), anyString()))
                        .willReturn(List.of(concertCode1, concertCode2));
                given(concertDataSyncPort.fetchConcertDetail(hallId, concertCode1))
                        .willReturn(concert1);
                given(concertDataSyncPort.fetchConcertDetail(hallId, concertCode2))
                        .willReturn(concert2);

                // when
                scheduler.fetchDailyConcertInfoList("20260421");

                // then
                verify(concertSyncService, times(1)).processConcertUpsert(concert1);
                verify(concertSyncService, times(1)).processConcertUpsert(concert2);
            }
        }

        @Nested
        @DisplayName("공연 코드가 없을 때")
        class Context_공연_코드_없을_때 {

            @Test
            @DisplayName("processConcertUpsert를 호출하지 않는다")
            void 공演_코드_없으면_upsert_호출하지_않는다() {
                // given
                String hallId = "FC001114-1";

                given(concertHallRepository.findAllIds()).willReturn(List.of(hallId));
                given(concertDataSyncPort.fetchDailyConcertCodes(anyString(), anyString(), anyString(), anyString()))
                        .willReturn(List.of());

                // when
                scheduler.fetchDailyConcertInfoList("20260421");

                // then
                verify(concertSyncService, times(0)).processConcertUpsert(org.mockito.ArgumentMatchers.any());
            }
        }
    }
}
