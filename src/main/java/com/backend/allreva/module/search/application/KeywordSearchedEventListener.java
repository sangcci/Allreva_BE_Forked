package com.backend.allreva.module.search.application;

import com.backend.allreva.common.event.KeywordSearchedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeywordSearchedEventListener {

    private final PopularKeywordService popularKeywordService;

    @Async("taskExecutor")
    @EventListener
    public void onKeywordSearched(final KeywordSearchedEvent event) {
        popularKeywordService.updateKeywordCount(event.getKeyword());
    }
}
