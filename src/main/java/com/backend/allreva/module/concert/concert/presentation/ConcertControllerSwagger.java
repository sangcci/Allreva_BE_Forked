package com.backend.allreva.module.concert.concert.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.concert.concert.application.dto.ConcertDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "공연 API", description = "공연 API")
public interface ConcertControllerSwagger {

    @Operation(summary = "공연 상세 조회", description = "공연 상세 조회 API")
    Response<ConcertDetailResponse> findConcertDetail(Long concertId);
}
