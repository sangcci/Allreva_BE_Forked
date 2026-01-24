package com.backend.allreva.module.search.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.concert.hall.application.dto.ConcertHallMainResponse;
import com.backend.allreva.module.search.application.*;
import com.backend.allreva.module.search.application.dto.*;
import com.backend.allreva.module.search.domain.SortDirection;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Tag(name = "검색 API", description = "통합 검색 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchController {

    private final PopularKeywordService popularKeywordService;
    private final ConcertSearchService concertSearchService;
    private final ConcertHallSearchService concertHallSearchService;
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

    @Operation(summary = "콘서트 검색 더보기 API", description = """
            콘서트 검색어에 따라 관련도 순으로 무한 스크롤\s
            searchAfter1, searchAfter2에 이전 SearchAfter에 있는 값들을 순서대로 넣어주어야 합니다.
            """)
    @GetMapping("/concert/list")
    public Response<ConcertSearchListResponse> searchConcertList(
            @RequestParam @NotEmpty(message = "검색어를 입력해야 합니다.") final String query,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final String searchAfter1,
            @RequestParam(required = false) final String searchAfter2) {
        List<Object> searchAfter = Stream.of(searchAfter1, searchAfter2)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        ConcertSearchListResponse response = concertSearchService.searchConcertList(query, searchAfter, pageSize);
        return Response.onSuccess(response);
    }

    @Operation(summary = "전체 기간 콘서트 검색 API", description = """
            과거와 현재 모든 콘서트를 검색하는 API입니다.
            searchAfter1, searchAfter2에 이전 SearchAfter에 있는 값들을 순서대로 넣어주어야 합니다.
            """)
    @GetMapping("/concert/list/all")
    public Response<ConcertSearchListResponse> searchAllConcertList(
            @RequestParam @NotEmpty(message = "검색어를 입력해야 합니다.") final String query,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final String searchAfter1,
            @RequestParam(required = false) final String searchAfter2) {
        List<Object> searchAfter = Stream.of(searchAfter1, searchAfter2)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        ConcertSearchListResponse response = concertSearchService.searchAllConcertList(query, searchAfter, pageSize);
        return Response.onSuccess(response);
    }

    @Operation(summary = "전체 검색시 렌트 상위 2개 썸네일 API", description = "검색어에 따라 관련도 상위 2개의 썸네일에 필요한 정보를 출력")
    @GetMapping("/rents/")
    public Response<List<RentThumbnail>> searchRentThumbnail(@RequestParam final String query) {
        return Response.onSuccess(
                rentSearchService.searchRentThumbnails(query));
    }

    @Operation(summary = "렌트 검색 더보기 API", description = "검색어에 따라 관련도 순으로 무한 스크롤 searchAfter1, searchAfter2에 이전 SearchAfter에 있는 값들을 순서대로 넣어주어야 합니다.")
    @GetMapping("/rents/list")
    public Response<RentSearchListResponse> searchRentList(
            @RequestParam @NotEmpty(message = "검색어를 입력해야 합니다.") final String query,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final String searchAfter1,
            @RequestParam(required = false) final String searchAfter2) {
        List<Object> searchAfter = Stream.of(searchAfter1, searchAfter2)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return Response.onSuccess(
                rentSearchService.searchRentSearchList(query, searchAfter, pageSize));
    }

    @Operation(summary = "전체 검색시 수요조사 상위 2개 썸네일 API", description = "검색어에 따라 관련도 상위 2개의 썸네일에 필요한 정보를 출력")
    @GetMapping("/surveys/")
    public Response<List<SurveyThumbnail>> searchSurveyThumbnail(@RequestParam final String query) {
        return Response.onSuccess(
                surveySearchService.searchSurveyThumbnails(query));
    }

    @Operation(summary = "수요조사 검색 더보기 API", description = "검색어에 따라 관련도 순으로 무한 스크롤 searchAfter1, searchAfter2에 이전 SearchAfter에 있는 값들을 순서대로 넣어주어야 합니다.")
    @GetMapping("/surveys/list")
    public Response<SurveySearchListResponse> searchSurveyList(
            @RequestParam @NotEmpty(message = "검색어를 입력해야 합니다.") final String query,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final String searchAfter1,
            @RequestParam(required = false) final String searchAfter2) {
        List<Object> searchAfter = Stream.of(searchAfter1, searchAfter2)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return Response.onSuccess(
                surveySearchService.searchSurveyList(query, searchAfter, pageSize));
    }

    @Operation(summary = "메인 화면 콘서트 API", description = "searchAfter1, searchAfter2에 이전 SearchAfter에 있는 값들을 순서대로 넣어주어야 합니다.")
    @GetMapping("/concert/main")
    public Response<ConcertMainResponse> getConcertMainList(
            @RequestParam(defaultValue = "") final String region,
            @RequestParam(defaultValue = "DATE") final SortDirection sortDirection,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final String searchAfter1,
            @RequestParam(required = false) final String searchAfter2) {
        List<Object> searchAfter = Stream.of(searchAfter1, searchAfter2)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        ConcertMainResponse concertMain = concertSearchService.searchMainConcerts(region, searchAfter, pageSize, sortDirection);
        return Response.onSuccess(concertMain);
    }

    @Operation(summary = "메인 화면 공연장 API", description = "searchAfter1, searchAfter2, searchAfter3에 이전 SearchAfter에 있는 값들을 순서대로 넣어주어야 합니다.")
    @GetMapping("/concert-hall/main")
    public Response<ConcertHallMainResponse> getConcertHallMainList(
            @RequestParam(defaultValue = "") final String address,
            @RequestParam(defaultValue = "0") final int seatScale,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final String searchAfter1,
            @RequestParam(required = false) final String searchAfter2,
            @RequestParam(required = false) final String searchAfter3) {
        List<Object> searchAfter = Stream.of(searchAfter1, searchAfter2, searchAfter3)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        ConcertHallMainResponse concertHallMain = concertHallSearchService.searchMainConcertHalls(
                address, seatScale, searchAfter, pageSize);
        return Response.onSuccess(concertHallMain);
    }
}
