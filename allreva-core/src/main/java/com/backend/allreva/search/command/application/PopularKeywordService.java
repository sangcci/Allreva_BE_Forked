package com.backend.allreva.search.command.application;

import com.backend.allreva.search.command.implementation.PopularKeywordCounter;
import com.backend.allreva.search.command.implementation.PopularKeywordRankRefresher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PopularKeywordService {

    private final PopularKeywordCounter popularKeywordCounter;
    private final PopularKeywordRankRefresher popularKeywordRankRefresher;

    public void recordSearch(final String keyword) {
        popularKeywordCounter.increase(keyword);
    }

    public void updateRank() {
        popularKeywordRankRefresher.update();
    }

    public void refreshRank() {
        popularKeywordCounter.decayAll();
        popularKeywordRankRefresher.update();
    }
}
