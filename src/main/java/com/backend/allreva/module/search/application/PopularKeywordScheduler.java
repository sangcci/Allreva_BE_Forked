package com.backend.allreva.module.search.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class PopularKeywordScheduler {

    private final PopularKeywordService popularKeywordService;
    private static final String ONE_HOUR_CRON = "0 0 * * * *";

    @Scheduled(cron = ONE_HOUR_CRON)
    public void updatePopularKeywordRank() {
        try {
            LocalDateTime now = LocalDateTime.now();
            String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss"));
            popularKeywordService.decreaseAllKeywordCount();
            popularKeywordService.updatePopularKeywordRank();
            log.info(" {} : popular keyword rank updatePreviewMessage complete", formattedDate);
        } catch (Exception e) {
            log.error("can't updatePreviewMessage popular keyword rank . Message: {}", e.getMessage());
        }
    }
}
