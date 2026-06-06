package com.backend.allreva.search;

import com.backend.allreva.common.web.response.View;
import com.backend.allreva.search.query.model.PopularKeywordResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(name = "검색 API", description = "통합 검색 Query API")
public interface SearchQueryControllerSwagger {

    @Operation(summary = "인기 검색어 Top 10 조회")
    View<List<PopularKeywordResult>> getPopularKeywordRank();
}
