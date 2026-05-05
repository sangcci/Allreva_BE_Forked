package com.backend.allreva.module.search.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.search.application.dto.PopularKeywordResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(name = "검색 API", description = "통합 검색 관련 API")
public interface SearchControllerSwagger {

    @Operation(summary = "인기 검색어 Top 10 조회")
    Response<List<PopularKeywordResponse>> getPopularKeywordRank();
}
