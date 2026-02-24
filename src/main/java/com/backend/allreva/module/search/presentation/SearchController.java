package com.backend.allreva.module.search.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.concert.place.application.dto.ConcertHallMainResponse;
import com.backend.allreva.module.search.application.ConcertSearchService;
import com.backend.allreva.module.search.application.PlaceSearchService;
import com.backend.allreva.module.search.application.PopularKeywordService;
import com.backend.allreva.module.search.application.RentSearchService;
import com.backend.allreva.module.search.application.SurveySearchService;
import com.backend.allreva.module.search.application.dto.ConcertMainResponse;
import com.backend.allreva.module.search.application.dto.ConcertSearchListResponse;
import com.backend.allreva.module.search.application.dto.ConcertThumbnail;
import com.backend.allreva.module.search.application.dto.PopularKeywordResponse;
import com.backend.allreva.module.search.application.dto.RentSearchListResponse;
import com.backend.allreva.module.search.application.dto.RentThumbnail;
import com.backend.allreva.module.search.application.dto.SortDirection;
import com.backend.allreva.module.search.application.dto.SurveySearchListResponse;
import com.backend.allreva.module.search.application.dto.SurveyThumbnail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "검색 API", description = "통합 검색 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchController {

    private final PopularKeywordService popularKeywordService;
    private final ConcertSearchService concertSearchService;
    private final PlaceSearchService placeSearchService;
    private final RentSearchService rentSearchService;
    private final SurveySearchService surveySearchService;

    @Operation(summary = "인기검색어 Top 10 조회", description = "인기검색어 Top 10을 조회합니다.")
    @GetMapping("/popular")
    public Response<List<PopularKeywordResponse>> getPopularKeywordRank() {
        return Response.onSuccess(
                popularKeywordService.getPopularKeywordRank());
    }

    @Operation(summary = "콘서트 검색시 상위 2개 썸네일 API", description = "콘서트 검색어에 따라 관련도 상위 2개의 썸네일에 필요한 정보를 출력")
    @GetMapping("/concert/")
    public Response<List<ConcertThumbnail>> searchConcertThumbnail(@RequestParam final String query) {
        return Response.onSuccess(
                concertSearchService.searchConcertThumbnails(query));
    }

    @Operation(summary = "콘서트 검색 더보기 API", description = "콘서트 검색어에 따라 관련도 순으로 무한 스크롤")
    @GetMapping("/concert/list")
    public Response<ConcertSearchListResponse> searchConcertList(
            @RequestParam @NotEmpty(message = "검색어를 입력해야 합니다.") final String query,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final Long cursorId) {
        return Response.onSuccess(concertSearchService.searchConcertList(query, cursorId, pageSize));
    }

    @Operation(summary = "전체 기간 콘서트 검색 API", description = "과거와 현재 모든 콘서트를 검색하는 API입니다.")
    @GetMapping("/concert/list/all")
    public Response<ConcertSearchListResponse> searchAllConcertList(
            @RequestParam @NotEmpty(message = "검색어를 입력해야 합니다.") final String query,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final Long cursorId) {
        return Response.onSuccess(concertSearchService.searchAllConcertList(query, cursorId, pageSize));
    }

    @Operation(summary = "전체 검색시 렌트 상위 2개 썸네일 API", description = "검색어에 따라 관련도 상위 2개의 썸네일에 필요한 정보를 출력")
    @GetMapping("/rents/")
    public Response<List<RentThumbnail>> searchRentThumbnail(@RequestParam final String query) {
        return Response.onSuccess(
                rentSearchService.searchRentThumbnails(query));
    }

    @Operation(summary = "렌트 검색 더보기 API", description = "검색어에 따라 관련도 순으로 무한 스크롤")
    @GetMapping("/rents/list")
    public Response<RentSearchListResponse> searchRentList(
            @RequestParam @NotEmpty(message = "검색어를 입력해야 합니다.") final String query,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final Long cursorId) {
        return Response.onSuccess(
                rentSearchService.searchRentSearchList(query, cursorId, pageSize));
    }

    @Operation(summary = "전체 검색시 수요조사 상위 2개 썸네일 API", description = "검색어에 따라 관련도 상위 2개의 썸네일에 필요한 정보를 출력")
    @GetMapping("/surveys/")
    public Response<List<SurveyThumbnail>> searchSurveyThumbnail(@RequestParam final String query) {
        return Response.onSuccess(
                surveySearchService.searchSurveyThumbnails(query));
    }

    @Operation(summary = "수요조사 검색 더보기 API", description = "검색어에 따라 관련도 순으로 무한 스크롤")
    @GetMapping("/surveys/list")
    public Response<SurveySearchListResponse> searchSurveyList(
            @RequestParam @NotEmpty(message = "검색어를 입력해야 합니다.") final String query,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final Long cursorId) {
        return Response.onSuccess(
                surveySearchService.searchSurveyList(query, cursorId, pageSize));
    }

    @Operation(summary = "메인 화면 콘서트 API", description = "지역별/정렬 기준으로 콘서트 목록 조회")
    @GetMapping("/concert/main")
    public Response<ConcertMainResponse> getConcertMainList(
            @RequestParam(defaultValue = "") final String region,
            @RequestParam(defaultValue = "DATE") final SortDirection sortDirection,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final Long cursorId) {
        return Response.onSuccess(concertSearchService.searchMainConcerts(region, cursorId, pageSize, sortDirection));
    }

    @Operation(summary = "메인 화면 공연장 API", description = "주소/좌석 기준으로 공연장 목록 조회")
    @GetMapping("/place/main")
    public Response<ConcertHallMainResponse> getPlaceMainList(
            @RequestParam(defaultValue = "") final String address,
            @RequestParam(defaultValue = "0") final int seatScale,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final String cursorId) {
        return Response.onSuccess(placeSearchService.searchMainPlaces(address, seatScale, cursorId, pageSize));
    }
}
