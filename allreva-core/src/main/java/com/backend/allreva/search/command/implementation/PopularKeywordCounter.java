package com.backend.allreva.search.command.implementation;

import com.backend.allreva.search.domain.PopularKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopularKeywordCounter {

    private static final double SEARCH_COUNT_INCREMENT = 1.0;

    private final PopularKeywordRepository popularKeywordRepository;

    public void increase(final String keyword) {
        popularKeywordRepository.recordSearch(keyword, SEARCH_COUNT_INCREMENT);
    }

    public void decayAll() {
        popularKeywordRepository.decreaseAllKeywordCount();
    }
}
