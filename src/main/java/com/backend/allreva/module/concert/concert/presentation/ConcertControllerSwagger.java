package com.backend.allreva.module.concert.concert.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.concert.concert.application.dto.ConcertDetailResponse;
import com.backend.allreva.module.concert.concert.application.dto.RelatedConcertResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "공연 API", description = "공연 API")
public interface ConcertControllerSwagger {

    @Operation(summary = "공연 상세 조회", description = "공연 상세 조회 API")
    Response<ConcertDetailResponse> getConcertDetail(@PathVariable("concertCode") String concertCode);

    @Operation(summary = "공연장 관련 공연 목록 조회", description = "공연장 코드 기준 관련 공연 무한 스크롤")
    Response<List<RelatedConcertResponse>> getRelatedConcerts(
            @RequestParam String hallCode,
            @RequestParam(required = false) String lastConcertCode,
            @RequestParam(defaultValue = "3") int pageSize);
}
