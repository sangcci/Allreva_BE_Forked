package com.backend.allreva.module.concert.concert.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.concert.concert.application.ConcertService;
import com.backend.allreva.module.concert.concert.application.dto.ConcertDetailResponse;
import com.backend.allreva.module.concert.concert.application.dto.RelatedConcertResponse;
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
    @GetMapping("/{concertCode}")
    public Response<ConcertDetailResponse> getConcertDetail(@PathVariable("concertCode") final String concertCode) {
        return Response.onSuccess(concertService.findDetailById(concertCode));
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
