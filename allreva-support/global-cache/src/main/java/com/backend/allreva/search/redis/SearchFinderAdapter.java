package com.backend.allreva.search.redis;

import com.backend.allreva.search.domain.PopularKeywordRanks;
import com.backend.allreva.search.domain.PopularKeywordRepository;
import com.backend.allreva.search.query.implementation.SearchFinderPort;
import com.backend.allreva.search.query.model.PopularKeywordResult;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SearchFinderAdapter implements SearchFinderPort {

    private final PopularKeywordRepository popularKeywordRepository;

    @Override
    public List<PopularKeywordResult> findPopularKeywordRank() {
        return popularKeywordRepository
                .getPopularKeywordRank()
                .map(PopularKeywordRanks::items)
                .orElse(Collections.emptyList())
                .stream()
                .map(PopularKeywordResult::from)
                .toList();
    }
}
