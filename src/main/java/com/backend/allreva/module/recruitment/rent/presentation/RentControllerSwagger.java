package com.backend.allreva.module.recruitment.rent.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.recruitment.rent.application.dto.HostedRentSummaryResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.JoinedRentResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentDetailResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentIdRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinIdRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinUpdateRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentParticipantResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentRegisterRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentSummaryResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentUpdateRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.SortType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "차 대절 API", description = "차 대절 관련 API")
public interface RentControllerSwagger {

    @Operation(summary = "차 대절 목록 조회", description = "무한 스크롤. 정렬 기준에 따라 커서 파라미터가 다름")
    Response<List<RentSummaryResponse>> getRentSummaries(
            String region, SortType sortType, Long lastId, LocalDate lastEndDate, @Min(10) int pageSize);

    @Operation(summary = "차 대절 상세 조회")
    Response<RentDetailResponse> getRentDetail(Long id);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "차 대절 등록", description = "**[HOST]**")
    Response<Long> registerRent(@Valid RentRegisterRequest rentRegisterRequest, Member member);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "차 대절 수정", description = "**[HOST]**")
    Response<Void> updateRent(@Valid RentUpdateRequest rentUpdateRequest, Member member);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "차 대절 마감", description = "**[HOST]**")
    Response<Void> closeRent(@Valid RentIdRequest rentIdRequest, Member member);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "차 대절 삭제", description = "**[HOST]**")
    Response<Void> deleteRent(@Valid RentIdRequest rentIdRequest, Member member);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "내가 개설한 차 대절 목록 조회", description = "**[HOST]** 무한 스크롤")
    Response<List<HostedRentSummaryResponse>> getRentHostedRentSummaries(
            Member member, Long lastId, @Min(10) int pageSize);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "내가 개설한 차 대절 상세 조회", description = "**[HOST]**")
    Response<List<RentParticipantResponse>> getHostedRentDetail(Long rentId, LocalDate boardingDate, Member member);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "차 대절 참여", description = "**[PARTICIPANT]**")
    Response<Long> joinRent(@Valid RentJoinRequest rentJoinRequest, Member member);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "차 대절 참여 수정", description = "**[PARTICIPANT]**")
    Response<Void> updateRentJoin(@Valid RentJoinUpdateRequest rentJoinUpdateRequest, Member member);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "차 대절 참여 취소", description = "**[PARTICIPANT]**")
    Response<Void> cancelRentJoin(@Valid RentJoinIdRequest rentJoinIdRequest, Member member);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "내가 참여한 차 대절 목록 조회", description = "**[PARTICIPANT]** 무한 스크롤")
    Response<List<JoinedRentResponse>> getJoinedRentSummaries(Member member, Long lastId, @Min(10) int pageSize);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "내가 참여한 차 대절 상세 조회", description = "**[PARTICIPANT]**")
    Response<RentParticipantResponse> getJoinedRentDetail(Long rentId, LocalDate boardingDate, Member member);
}
