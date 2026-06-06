package com.backend.allreva.concert.concert;

import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.common.web.response.View;
import com.backend.allreva.concert.concert.query.application.ConcertFinder;
import com.backend.allreva.concert.concert.query.model.ConcertDetailResult;
import com.backend.allreva.concert.concert.query.model.ConcertThumbnailResult;
import com.backend.allreva.concert.concert.query.model.RelatedConcertResult;
import com.backend.allreva.concert.concert.query.model.SortDirectionView;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/concerts")
@RestController
@Validated
public class ConcertQueryController implements ConcertQueryControllerSwagger {

    private final ConcertFinder concertQueryService;

    @Override
    @GetMapping("/main")
    public View<SliceResponse<ConcertThumbnailResult, String>> getMainConcerts(
            @RequestParam(defaultValue = "") final String region,
            @RequestParam(defaultValue = "DATE") final SortDirectionView sortDirection,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final String cursorCode) {
        return View.onSuccess(concertQueryService.getMainConcerts(region, cursorCode, pageSize, sortDirection));
    }

    @Override
    @GetMapping("/suggestions")
    public View<List<ConcertThumbnailResult>> getConcertSuggestions(@RequestParam final String query) {
        return View.onSuccess(concertQueryService.getConcertSuggestions(query));
    }

    @Override
    @GetMapping("/search")
    public View<SliceResponse<ConcertThumbnailResult, String>> searchConcerts(
            @RequestParam final String query,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final String cursorCode) {
        return View.onSuccess(concertQueryService.searchConcerts(query, cursorCode, pageSize));
    }

    @Override
    @GetMapping("/{concertCode}")
    public View<ConcertDetailResult> getConcertDetail(@PathVariable("concertCode") final String concertCode) {
        return View.onSuccess(concertQueryService.getConcertDetail(concertCode));
    }

    @Override
    @GetMapping
    public View<List<RelatedConcertResult>> getRelatedConcerts(
            @RequestParam final String hallCode,
            @RequestParam(required = false) final String lastConcertCode,
            @RequestParam(defaultValue = "3") final int pageSize) {
        return View.onSuccess(concertQueryService.getRelatedConcerts(hallCode, lastConcertCode, pageSize));
    }
}
