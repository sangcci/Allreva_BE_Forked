package com.backend.allreva.module.search.application.port;

import com.backend.allreva.module.search.application.dto.PopularKeywordResponses;

import java.util.List;

public interface PopularKeywordRepository {
    PopularKeywordResponses getPopularKeywordRank();

    void updatePopularKeywordRank(PopularKeywordResponses list);

    void updateKeywordCount(String keyword, Double count);

    List<String> getTop10Keywords();

    void decreaseAllKeywordCount();
}
