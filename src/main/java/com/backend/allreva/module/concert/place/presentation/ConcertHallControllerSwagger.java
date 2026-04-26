package com.backend.allreva.module.concert.place.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.concert.place.application.dto.ConcertHallDetailResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "공연장 API", description = "공연장 관련 API")
public interface ConcertHallControllerSwagger {

    @Operation(summary = "공연장 상세 조회", description = "공연장 상세 조회 API")
    Response<ConcertHallDetailResponse> getConcertHallDetail(@PathVariable("hallCode") String hallCode);
}
