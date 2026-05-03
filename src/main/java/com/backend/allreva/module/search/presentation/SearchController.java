package com.backend.allreva.module.search.presentation;

import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.concert.place.application.dto.ConcertHallMainResponse;
import com.backend.allreva.module.search.application.ConcertSearchService;
import com.backend.allreva.module.search.application.PlaceSearchService;
import com.backend.allreva.module.search.application.PopularKeywordService;
import com.backend.allreva.module.search.application.RentSearchService;
import com.backend.allreva.module.search.application.SurveySearchService;
import com.backend.allreva.module.search.application.dto.ConcertThumbnail;
import com.backend.allreva.module.search.application.dto.PopularKeywordResponse;
import com.backend.allreva.module.search.application.dto.RentThumbnail;
import com.backend.allreva.module.search.application.dto.SortDirection;
import com.backend.allreva.module.search.application.dto.SurveyThumbnail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
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
    @GetMapping("/concert/suggestions")
    public Response<List<ConcertThumbnail>> getConcertSuggestions(
            @NotBlank(message = "검색어를 입력해야 합니다.") @RequestParam final String query) {
        return Response.onSuccess(concertSearchService.getConcertSuggestions(query));
    }

    @Override
    @GetMapping("/concert")
    public Response<SliceResponse<ConcertThumbnail, String>> searchConcerts(
            @NotBlank(message = "검색어를 입력해야 합니다.") @RequestParam final String query,
            @Positive @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final String cursorCode) {
        return Response.onSuccess(concertSearchService.searchConcerts(query, cursorCode, pageSize));
    }

    @Override
    @GetMapping("/rents/suggestions")
    public Response<List<RentThumbnail>> getRentSuggestions(
            @NotBlank(message = "검색어를 입력해야 합니다.") @RequestParam final String query) {
        return Response.onSuccess(rentSearchService.getRentSuggestions(query));
    }

    @Override
    @GetMapping("/rents")
    public Response<SliceResponse<RentThumbnail, Long>> searchRents(
            @NotBlank(message = "검색어를 입력해야 합니다.") @RequestParam final String query,
            @Positive @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final Long cursorId) {
        return Response.onSuccess(rentSearchService.searchRents(query, cursorId, pageSize));
    }

    @Override
    @GetMapping("/surveys/suggestions")
    public Response<List<SurveyThumbnail>> getSurveySuggestions(
            @NotBlank(message = "검색어를 입력해야 합니다.") @RequestParam final String query) {
        return Response.onSuccess(surveySearchService.getSurveySuggestions(query));
    }

    @Override
    @GetMapping("/surveys")
    public Response<SliceResponse<SurveyThumbnail, Long>> searchSurveys(
            @NotBlank(message = "검색어를 입력해야 합니다.") @RequestParam final String query,
            @Positive @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final Long cursorId) {
        return Response.onSuccess(surveySearchService.searchSurveys(query, cursorId, pageSize));
    }

    @Override
    @GetMapping("/concert/main")
    public Response<SliceResponse<ConcertThumbnail, String>> getMainConcerts(
            @RequestParam(defaultValue = "") final String region,
            @RequestParam(defaultValue = "DATE") final SortDirection sortDirection,
            @Positive @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final String cursorCode) {
        return Response.onSuccess(concertSearchService.getMainConcerts(region, cursorCode, pageSize, sortDirection));
    }

    @Override
    @GetMapping("/place/main")
    public Response<ConcertHallMainResponse> getPlaceMainList(
            @RequestParam(defaultValue = "") final String address,
            @RequestParam(defaultValue = "0") final int seatScale,
            @Positive @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final String cursorId) {
        return Response.onSuccess(placeSearchService.searchMainPlaces(address, seatScale, cursorId, pageSize));
    }
}
