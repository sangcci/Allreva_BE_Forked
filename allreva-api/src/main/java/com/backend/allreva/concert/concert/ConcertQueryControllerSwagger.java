package com.backend.allreva.concert.concert;

import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.common.web.response.View;
import com.backend.allreva.concert.concert.query.model.ConcertDetailResult;
import com.backend.allreva.concert.concert.query.model.ConcertThumbnailResult;
import com.backend.allreva.concert.concert.query.model.RelatedConcertResult;
import com.backend.allreva.concert.concert.query.model.SortDirectionView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "공연 API", description = "공연 Query API")
public interface ConcertQueryControllerSwagger {

    @Operation(summary = "메인 콘서트 목록 조회", description = "지역별/정렬 기준으로 콘서트 목록 조회")
    View<SliceResponse<ConcertThumbnailResult, String>> getMainConcerts(
            String region, SortDirectionView sortDirection, @Positive int pageSize, String cursorCode);

    @Operation(summary = "콘서트 자동완성 제안", description = "검색어 관련도 상위 2개")
    View<List<ConcertThumbnailResult>> getConcertSuggestions(@NotBlank @RequestParam String query);

    @Operation(summary = "콘서트 검색 목록 조회", description = "무한 스크롤. 관련도 순 정렬")
    View<SliceResponse<ConcertThumbnailResult, String>> searchConcerts(
            @NotBlank @RequestParam String query, @Positive int pageSize, String cursorCode);

    @Operation(summary = "공연 상세 조회")
    View<ConcertDetailResult> getConcertDetail(@NotBlank @PathVariable("concertCode") String concertCode);

    @Operation(summary = "공연장 관련 공연 목록 조회", description = "공연장 코드 기준 관련 공연 무한 스크롤")
    View<List<RelatedConcertResult>> getRelatedConcerts(
            @NotBlank @RequestParam String hallCode,
            @RequestParam(required = false) String lastConcertCode,
            @Positive @RequestParam(defaultValue = "3") int pageSize);
}
