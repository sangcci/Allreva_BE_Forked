package com.backend.allreva.module.search.presentation;

import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.concert.place.application.dto.ConcertHallMainResponse;
import com.backend.allreva.module.search.application.dto.ConcertThumbnail;
import com.backend.allreva.module.search.application.dto.PopularKeywordResponse;
import com.backend.allreva.module.search.application.dto.RentThumbnail;
import com.backend.allreva.module.search.application.dto.SortDirection;
import com.backend.allreva.module.search.application.dto.SurveyThumbnail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;

@Tag(name = "검색 API", description = "통합 검색 관련 API")
public interface SearchControllerSwagger {

    @Operation(summary = "인기 검색어 Top 10 조회")
    Response<List<PopularKeywordResponse>> getPopularKeywordRank();

    @Operation(summary = "콘서트 자동완성 제안", description = "검색어 관련도 상위 2개")
    Response<List<ConcertThumbnail>> getConcertSuggestions(@NotBlank(message = "검색어를 입력해야 합니다.") String query);

    @Operation(summary = "콘서트 검색 목록 조회", description = "무한 스크롤. 관련도 순 정렬")
    Response<SliceResponse<ConcertThumbnail, String>> searchConcerts(
            @NotBlank(message = "검색어를 입력해야 합니다.") String query, @Positive int pageSize, String cursorCode);

    @Operation(summary = "차 대절 자동완성 제안", description = "검색어 관련도 상위 2개")
    Response<List<RentThumbnail>> getRentSuggestions(@NotBlank(message = "검색어를 입력해야 합니다.") String query);

    @Operation(summary = "차 대절 검색 목록 조회", description = "무한 스크롤. 관련도 순 정렬")
    Response<SliceResponse<RentThumbnail, Long>> searchRents(
            @NotBlank(message = "검색어를 입력해야 합니다.") String query, @Positive int pageSize, Long cursorId);

    @Operation(summary = "수요조사 자동완성 제안", description = "검색어 관련도 상위 2개")
    Response<List<SurveyThumbnail>> getSurveySuggestions(@NotBlank(message = "검색어를 입력해야 합니다.") String query);

    @Operation(summary = "수요조사 검색 목록 조회", description = "무한 스크롤. 관련도 순 정렬")
    Response<SliceResponse<SurveyThumbnail, Long>> searchSurveys(
            @NotBlank(message = "검색어를 입력해야 합니다.") String query, @Positive int pageSize, Long cursorId);

    @Operation(summary = "메인 콘서트 목록 조회", description = "지역별/정렬 기준으로 콘서트 목록 조회")
    Response<SliceResponse<ConcertThumbnail, String>> getMainConcerts(
            String region, SortDirection sortDirection, @Positive int pageSize, String cursorCode);

    @Operation(summary = "메인 공연장 목록 조회", description = "주소/좌석 기준으로 공연장 목록 조회")
    Response<ConcertHallMainResponse> getPlaceMainList(
            String address, int seatScale, @Positive int pageSize, String cursorId);
}
