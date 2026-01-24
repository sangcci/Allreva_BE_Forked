package com.backend.allreva.module.member.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.member.application.dto.MemberDetailResponse;
import com.backend.allreva.module.member.application.dto.MemberRegisterRequest;
import com.backend.allreva.module.member.application.dto.RefundAccountRequest;
import com.backend.allreva.module.member.domain.Member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "회원 API", description = "회원 정보를 관리하는 API")
public interface MemberControllerSwagger {

    @Operation(summary = "회원 정보 조회 API", description = "회원 정보를 조회합니다.")
    Response<MemberDetailResponse> getMemberDetail(
            Member member);

    @Operation(summary = "닉네임 중복 확인 API", description = "닉네임 중복을 확인합니다.")
    Response<Boolean> isDuplicatedNickname(
            String nickname);

    @Operation(summary = "회원 가입 API", description = "회원을 등록합니다.")
    Response<Void> registerMember(
            MemberRegisterRequest memberRegisterRequest);

    @Operation(summary = "회원 프로필 수정 API", description = "회원 프로필을 수정합니다.")
    Response<Void> updateMemberInfo(
            Member member,
            MemberRegisterRequest memberRegisterRequest);

    @Operation(summary = "회원 환불 계좌 등록 API", description = "회원 환불 계좌를 등록합니다.")
    Response<Void> registerRefundAccount(
            Member member,
            RefundAccountRequest refundAccountRequest);

    @Operation(summary = "회원 환불 계좌 삭제 API", description = "회원 환불 계좌를 삭제합니다.")
    Response<Void> deleteRefundAccount(
            Member member);
}
