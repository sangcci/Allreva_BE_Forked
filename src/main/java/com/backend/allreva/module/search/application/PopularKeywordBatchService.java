package com.backend.allreva.module.search.application;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopularKeywordBatchService {

    private final PopularKeywordService popularKeywordService;

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
