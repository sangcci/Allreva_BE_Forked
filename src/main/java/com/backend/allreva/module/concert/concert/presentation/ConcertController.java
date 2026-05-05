package com.backend.allreva.module.concert.concert.presentation;

import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.concert.concert.application.ConcertService;
import com.backend.allreva.module.concert.concert.application.dto.ConcertDetailResponse;
import com.backend.allreva.module.concert.concert.application.dto.ConcertThumbnail;
import com.backend.allreva.module.concert.concert.application.dto.RelatedConcertResponse;
import com.backend.allreva.module.concert.concert.application.dto.SortDirection;
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
public class ConcertController implements ConcertControllerSwagger {

    private final ConcertService concertService;

    @Override
    @GetMapping("/main")
    public Response<SliceResponse<ConcertThumbnail, String>> getMainConcerts(
            @RequestParam(defaultValue = "") final String region,
            @RequestParam(defaultValue = "DATE") final SortDirection sortDirection,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final String cursorCode) {
        return Response.onSuccess(concertService.getMainConcerts(region, cursorCode, pageSize, sortDirection));
    }

    @Override
    @GetMapping("/suggestions")
    public Response<List<ConcertThumbnail>> getConcertSuggestions(@RequestParam final String query) {
        return Response.onSuccess(concertService.getConcertSuggestions(query));
    }

    @Override
    @GetMapping("/search")
    public Response<SliceResponse<ConcertThumbnail, String>> searchConcerts(
            @RequestParam final String query,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final String cursorCode) {
        return Response.onSuccess(concertService.searchConcerts(query, cursorCode, pageSize));
    }

    @Override
    @GetMapping("/{concertCode}")
    public Response<ConcertDetailResponse> getConcertDetail(@PathVariable("concertCode") final String concertCode) {
        return Response.onSuccess(concertService.getConcertDetail(concertCode));
    }

    @Override
    @GetMapping
    public Response<List<RelatedConcertResponse>> getRelatedConcerts(
            @RequestParam final String hallCode,
            @RequestParam(required = false) final String lastConcertCode,
            @RequestParam(defaultValue = "3") final int pageSize) {
        return Response.onSuccess(concertService.getRelatedConcerts(hallCode, lastConcertCode, pageSize));
    }
}
