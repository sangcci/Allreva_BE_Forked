package com.backend.allreva.batch.scheduler.concert;

import com.backend.allreva.module.concert.place.application.ConcertHallSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class ConcertHallSyncScheduler {

    private final ConcertHallSyncService concertHallSyncService;

    /** 공연장 정보 매월 동기화 — 매월 1일 새벽 2시 */
    @Scheduled(cron = "0 0 2 1 * *")
    public void fetchMonthlyHallInfoList() {
        try {
            concertHallSyncService.fetchConcertHallInfoList();
            log.info("Monthly concert hall info update complete");
        } catch (Exception e) {
            log.error("Can't update monthly hall info. Message: {}", e.getMessage());
        }
    }
}
