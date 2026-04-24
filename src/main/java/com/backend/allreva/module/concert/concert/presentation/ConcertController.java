package com.backend.allreva.module.concert.concert.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.concert.concert.application.ConcertService;
import com.backend.allreva.module.concert.concert.application.dto.ConcertDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/concerts")
@RestController
@Validated
public class ConcertController implements ConcertControllerSwagger {

    private final ConcertService concertService;

    @Operation(summary = "공연 상세 조회", description = "공연 상세 조회 API")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json"))})
    @GetMapping("/{concertCode}")
    public Response<ConcertDetailResponse> findConcertDetail(@PathVariable("concertCode") final String concertCode) {
        ConcertDetailResponse detail = concertService.findDetailById(concertCode);
        return Response.onSuccess(detail);
    }
}
