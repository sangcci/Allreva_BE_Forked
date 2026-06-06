package com.backend.allreva.batch.search;

import com.backend.allreva.search.command.application.PopularKeywordService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PopularKeywordScheduler {

    private static final String ONE_HOUR_CRON = "0 0 * * * *";

    private final PopularKeywordService popularKeywordService;

    @Scheduled(cron = ONE_HOUR_CRON)
    public void refreshRank() {
        try {
            LocalDateTime now = LocalDateTime.now();
            String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss"));
            popularKeywordService.refreshRank();
            log.info("{}: popular keyword rank refresh complete", formattedDate);
        } catch (Exception e) {
            log.error("Can't refresh popular keyword rank. Message: {}", e.getMessage(), e);
        }
    }
}
