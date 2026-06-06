package com.backend.allreva.search.query.implementation;

import com.backend.allreva.search.query.model.PopularKeywordResult;
import java.util.List;

public interface SearchFinderPort {

    List<PopularKeywordResult> findPopularKeywordRank();
}
