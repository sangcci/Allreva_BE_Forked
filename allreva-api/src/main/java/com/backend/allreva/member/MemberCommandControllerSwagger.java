package com.backend.allreva.member;

import com.backend.allreva.common.web.response.View;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.member.request.MemberInfoUpdateRequest;
import com.backend.allreva.member.request.MemberRegisterRequest;
import com.backend.allreva.member.request.RefundAccountRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "회원 API", description = "회원 Command API")
public interface MemberCommandControllerSwagger {

    @Operation(summary = "회원 가입")
    View<Void> registerMember(MemberRegisterRequest request);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "회원 프로필 수정", description = "**[회원]**")
    View<Void> updateMemberInfo(Member member, MemberInfoUpdateRequest request);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "환불 계좌 수정", description = "**[회원]**")
    View<Void> updateRefundAccount(Member member, RefundAccountRequest request);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "환불 계좌 초기화", description = "**[회원]**")
    View<Void> resetRefundAccount(Member member);
}
