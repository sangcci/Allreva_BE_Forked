package com.backend.allreva.batch.concert;

import com.backend.allreva.concert.place.command.application.ConcertHallSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class ConcertHallSyncScheduler {

    private final ConcertHallSyncService concertHallSyncService;

    @Scheduled(cron = "0 0 2 1 * *")
    public void syncMonthlyHalls() {
        try {
            concertHallSyncService.sync();
            log.info("Monthly concert hall sync complete");
        } catch (Exception e) {
            log.error("Can't sync monthly concert halls. Message: {}", e.getMessage());
        }
    }
}
