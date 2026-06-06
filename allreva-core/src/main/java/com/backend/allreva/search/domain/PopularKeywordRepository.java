package com.backend.allreva.search.domain;

import java.util.List;
import java.util.Optional;

public interface PopularKeywordRepository {
    Optional<PopularKeywordRanks> getPopularKeywordRank();

    void updateRank(PopularKeywordRanks list);

    void recordSearch(String keyword, Double count);

    List<String> getTop10Keywords();

    void decreaseAllKeywordCount();
}
