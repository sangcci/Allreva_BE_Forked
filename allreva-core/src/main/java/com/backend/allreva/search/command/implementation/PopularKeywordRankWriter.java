package com.backend.allreva.search.command.implementation;

import com.backend.allreva.search.domain.PopularKeywordRanks;
import com.backend.allreva.search.domain.PopularKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopularKeywordRankWriter {

    private final PopularKeywordRepository popularKeywordRepository;

    public void update(final PopularKeywordRanks ranks) {
        popularKeywordRepository.updateRank(ranks);
    }
}
