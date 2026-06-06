package com.backend.allreva.recruitment.rent;

import com.backend.allreva.common.web.response.View;
import com.backend.allreva.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "차 대절 API", description = "차 대절 Command API")
public interface RentCommandControllerSwagger {

    @SecurityRequirement(name = "USER")
    @Operation(summary = "차 대절 등록", description = "**[HOST]**")
    View<Long> registerRent(RentRegisterRequest request, Member member);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "차 대절 수정", description = "**[HOST]**")
    View<Void> updateRent(RentUpdateRequest request, Member member);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "차 대절 마감", description = "**[HOST]**")
    View<Void> closeRent(RentIdRequest request, Member member);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "차 대절 삭제", description = "**[HOST]**")
    View<Void> deleteRent(RentIdRequest request, Member member);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "차 대절 참여", description = "**[PARTICIPANT]**")
    View<Long> joinRent(RentJoinRequest request, Member member);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "차 대절 참여 수정", description = "**[PARTICIPANT]**")
    View<Void> updateRentJoin(RentJoinUpdateRequest request, Member member);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "차 대절 참여 취소", description = "**[PARTICIPANT]**")
    View<Void> cancelRentJoin(RentJoinIdRequest request, Member member);
}
