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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchController implements SearchControllerSwagger {

    private final PopularKeywordService popularKeywordService;
    private final ConcertSearchService concertSearchService;
    private final PlaceSearchService placeSearchService;
    private final RentSearchService rentSearchService;
    private final SurveySearchService surveySearchService;

    @Override
    @GetMapping("/popular")
    public Response<List<PopularKeywordResponse>> getPopularKeywordRank() {
        return Response.onSuccess(popularKeywordService.getPopularKeywordRank());
    }

    @Override
    @GetMapping("/concert/")
    public Response<List<ConcertThumbnail>> searchConcertThumbnail(@RequestParam final String query) {
        return Response.onSuccess(concertSearchService.searchConcertThumbnails(query));
    }

    @Override
    @GetMapping("/concert/list")
    public Response<ConcertSearchListResponse> searchConcertList(
            @RequestParam final String query,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final String cursorCode) {
        return Response.onSuccess(concertSearchService.searchConcertList(query, cursorCode, pageSize));
    }

    @Override
    @GetMapping("/concert/list/all")
    public Response<ConcertSearchListResponse> searchAllConcertList(
            @RequestParam final String query,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final String cursorCode) {
        return Response.onSuccess(concertSearchService.searchAllConcertList(query, cursorCode, pageSize));
    }

    @Override
    @GetMapping("/rents/")
    public Response<List<RentThumbnail>> searchRentThumbnail(@RequestParam final String query) {
        return Response.onSuccess(rentSearchService.searchRentThumbnails(query));
    }

    @Override
    @GetMapping("/rents/list")
    public Response<RentSearchListResponse> searchRentList(
            @RequestParam final String query,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final Long cursorId) {
        return Response.onSuccess(rentSearchService.searchRentSearchList(query, cursorId, pageSize));
    }

    @Override
    @GetMapping("/surveys/")
    public Response<List<SurveyThumbnail>> searchSurveyThumbnail(@RequestParam final String query) {
        return Response.onSuccess(surveySearchService.searchSurveyThumbnails(query));
    }

    @Override
    @GetMapping("/surveys/list")
    public Response<SurveySearchListResponse> searchSurveyList(
            @RequestParam final String query,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final Long cursorId) {
        return Response.onSuccess(surveySearchService.searchSurveyList(query, cursorId, pageSize));
    }

    @Override
    @GetMapping("/concert/main")
    public Response<ConcertMainResponse> getConcertMainList(
            @RequestParam(defaultValue = "") final String region,
            @RequestParam(defaultValue = "DATE") final SortDirection sortDirection,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final String cursorCode) {
        return Response.onSuccess(concertSearchService.searchMainConcerts(region, cursorCode, pageSize, sortDirection));
    }

    @Override
    @GetMapping("/place/main")
    public Response<ConcertHallMainResponse> getPlaceMainList(
            @RequestParam(defaultValue = "") final String address,
            @RequestParam(defaultValue = "0") final int seatScale,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final String cursorId) {
        return Response.onSuccess(placeSearchService.searchMainPlaces(address, seatScale, cursorId, pageSize));
    }
}
