package com.backend.allreva.module.member.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.member.application.dto.MemberDetailResponse;
import com.backend.allreva.module.member.application.dto.MemberRegisterRequest;
import com.backend.allreva.module.member.application.dto.RefundAccountRequest;
import com.backend.allreva.module.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "회원 API", description = "회원 관련 API")
public interface MemberControllerSwagger {

    @SecurityRequirement(name = "USER")
    @Operation(summary = "회원 정보 조회", description = "**[회원]**")
    Response<MemberDetailResponse> getMemberDetail(Member member);

    @Operation(summary = "닉네임 중복 확인")
    Response<Boolean> isDuplicatedNickname(String nickname);

    @Operation(summary = "회원 가입")
    Response<Void> registerMember(MemberRegisterRequest memberRegisterRequest);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "회원 프로필 수정", description = "**[회원]**")
    Response<Void> updateMemberInfo(Member member, MemberRegisterRequest memberRegisterRequest);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "환불 계좌 등록", description = "**[회원]**")
    Response<Void> registerRefundAccount(Member member, RefundAccountRequest refundAccountRequest);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "환불 계좌 삭제", description = "**[회원]**")
    Response<Void> deleteRefundAccount(Member member);
}
