package com.backend.allreva.search.command.implementation;

import com.backend.allreva.search.command.application.PopularKeywordService;
import com.backend.allreva.search.domain.KeywordSearchedEvent;
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
        popularKeywordService.recordSearch(event.getKeyword());
    }
}
