package com.backend.allreva.batch.concert;

import com.backend.allreva.concert.concert.command.application.ConcertSyncService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConcertSyncScheduler {

    private final ConcertSyncService concertSyncService;

    @Scheduled(cron = "0 0 4 * * *")
    public void fetchDailyScheduled() {
        LocalDate today = LocalDate.now();
        try {
            concertSyncService.sync(today);
            log.info("{}: daily concert info update complete", today);
        } catch (Exception e) {
            log.error("Can't update daily concert info. Message: {}", e.getMessage());
        }
    }
}
