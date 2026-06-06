package com.backend.allreva.recruitment.rent;

import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.common.web.response.View;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.recruitment.rent.domain.SortType;
import com.backend.allreva.recruitment.rent.query.model.HostedRentSummaryResult;
import com.backend.allreva.recruitment.rent.query.model.JoinedRentResult;
import com.backend.allreva.recruitment.rent.query.model.RentDetailResult;
import com.backend.allreva.recruitment.rent.query.model.RentParticipantResult;
import com.backend.allreva.recruitment.rent.query.model.RentSummaryResult;
import com.backend.allreva.recruitment.rent.query.model.RentThumbnailResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "차 대절 API", description = "차 대절 Query API")
public interface RentQueryControllerSwagger {

    @Operation(summary = "차 대절 자동완성 제안", description = "검색어 관련도 상위 2개")
    View<List<RentThumbnailResult>> getRentSuggestions(String query);

    @Operation(summary = "차 대절 검색 목록 조회", description = "무한 스크롤. 관련도 순 정렬")
    View<SliceResponse<RentThumbnailResult, Long>> searchRents(String query, @Min(1) int pageSize, Long cursorId);

    @Operation(summary = "차 대절 목록 조회", description = "무한 스크롤. 정렬 기준에 따라 커서 파라미터가 다름")
    View<List<RentSummaryResult>> getRentSummaries(
            String region, SortType sortType, Long lastId, LocalDate lastEndDate, @Min(10) int pageSize);

    @Operation(summary = "차 대절 상세 조회")
    View<RentDetailResult> getRentDetail(Long id);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "내가 개설한 차 대절 목록 조회", description = "**[HOST]** 무한 스크롤")
    View<List<HostedRentSummaryResult>> getRentHostedRentSummaries(Member member, Long lastId, @Min(10) int pageSize);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "내가 개설한 차 대절 상세 조회", description = "**[HOST]**")
    View<List<RentParticipantResult>> getHostedRentDetail(Long rentId, LocalDate boardingDate, Member member);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "내가 참여한 차 대절 목록 조회", description = "**[PARTICIPANT]** 무한 스크롤")
    View<List<JoinedRentResult>> getJoinedRentSummaries(Member member, Long lastId, @Min(10) int pageSize);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "내가 참여한 차 대절 상세 조회", description = "**[PARTICIPANT]**")
    View<RentParticipantResult> getJoinedRentDetail(Long rentId, LocalDate boardingDate, Member member);
}
