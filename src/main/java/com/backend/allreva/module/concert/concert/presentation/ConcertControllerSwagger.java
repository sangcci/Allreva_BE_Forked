package com.backend.allreva.module.concert.concert.presentation;

import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.concert.concert.application.dto.ConcertDetailResponse;
import com.backend.allreva.module.concert.concert.application.dto.ConcertThumbnail;
import com.backend.allreva.module.concert.concert.application.dto.RelatedConcertResponse;
import com.backend.allreva.module.concert.concert.application.dto.SortDirection;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "공연 API", description = "공연 관련 API")
public interface ConcertControllerSwagger {

    @Operation(summary = "메인 콘서트 목록 조회", description = "지역별/정렬 기준으로 콘서트 목록 조회")
    Response<SliceResponse<ConcertThumbnail, String>> getMainConcerts(
            String region, SortDirection sortDirection, @Positive int pageSize, String cursorCode);

    @Operation(summary = "콘서트 자동완성 제안", description = "검색어 관련도 상위 2개")
    Response<List<ConcertThumbnail>> getConcertSuggestions(@NotBlank @RequestParam String query);

    @Operation(summary = "콘서트 검색 목록 조회", description = "무한 스크롤. 관련도 순 정렬")
    Response<SliceResponse<ConcertThumbnail, String>> searchConcerts(
            @NotBlank @RequestParam String query, @Positive int pageSize, String cursorCode);

    @Operation(summary = "공연 상세 조회")
    Response<ConcertDetailResponse> getConcertDetail(@NotBlank @PathVariable("concertCode") String concertCode);

    @Operation(summary = "공연장 관련 공연 목록 조회", description = "공연장 코드 기준 관련 공연 무한 스크롤")
    Response<List<RelatedConcertResponse>> getRelatedConcerts(
            @NotBlank @RequestParam String hallCode,
            @RequestParam(required = false) String lastConcertCode,
            @Positive @RequestParam(defaultValue = "3") int pageSize);
}
