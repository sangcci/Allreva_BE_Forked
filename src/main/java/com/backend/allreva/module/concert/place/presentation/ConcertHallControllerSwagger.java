package com.backend.allreva.module.concert.place.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.concert.place.application.dto.ConcertHallDetailResponse;
import com.backend.allreva.module.concert.place.application.dto.RelatedConcertResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "공연장 API", description = "공연장 관련 API")
public interface ConcertHallControllerSwagger {

    @Operation(summary = "공연장 상세 조회", description = "공연장 상세 조회 API")
    @GetMapping("/{hallCode}")
    Response<ConcertHallDetailResponse> findHallDetailByHallCode(@PathVariable("hallCode") String hallCode);

    @Operation(summary = "콘서트 홀에 관련 공연 API", description = "콘서트 홀에 관련 공연 API 무한 스크롤")
    @GetMapping("/relate/list")
    Response<List<RelatedConcertResponse>> findRelatedConcertList(
            @RequestParam(required = true) String hallCode,
            @RequestParam(required = false) Long lastId,
            @RequestParam(required = false) Long lastViewCount,
            @RequestParam(defaultValue = "3") int pageSize);
}
