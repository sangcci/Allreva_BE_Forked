package com.backend.allreva.batch.scheduler.search;

import com.backend.allreva.module.search.application.PopularKeywordBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopularKeywordScheduler {

    private static final String ONE_HOUR_CRON = "0 0 * * * *";

    private final PopularKeywordBatchService popularKeywordBatchService;

    @Scheduled(cron = ONE_HOUR_CRON)
    public void updatePopularKeywordRank() {
        popularKeywordBatchService.updatePopularKeywordRank();
    }
}
