package com.backend.allreva.search.command.implementation;

import com.backend.allreva.search.domain.PopularKeywordRankItem;
import com.backend.allreva.search.domain.PopularKeywordRanks;
import com.backend.allreva.search.domain.PopularKeywordRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopularKeywordRankReader {

    private final PopularKeywordRepository popularKeywordRepository;

    public List<String> currentKeywords() {
        return popularKeywordRepository
                .getPopularKeywordRank()
                .map(PopularKeywordRanks::items)
                .orElse(Collections.emptyList())
                .stream()
                .map(PopularKeywordRankItem::keyword)
                .toList();
    }

    public List<String> topKeywords() {
        return popularKeywordRepository.getTop10Keywords();
    }
}
