package com.backend.allreva.module.member.presentation;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.member.application.MemberService;
import com.backend.allreva.module.member.application.dto.MemberDetailResponse;
import com.backend.allreva.module.member.application.dto.MemberRegisterRequest;
import com.backend.allreva.module.member.application.dto.RefundAccountRequest;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.auth.security.AuthMember;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/members")
public class MemberController implements MemberControllerSwagger {

    private final MemberService memberService;

    @GetMapping
    public Response<MemberDetailResponse> getMemberDetail(
            @AuthMember final Member member) {
        return Response.onSuccess(memberService.getById(member.getId()));
    }

    @GetMapping("/check-nickname")
    public Response<Boolean> isDuplicatedNickname(
            @RequestParam final String nickname) {
        return Response.onSuccess(memberService.isDuplicatedNickname(nickname).isDuplicated());
    }

    @PostMapping("/register")
    public Response<Void> registerMember(
            @RequestBody final MemberRegisterRequest memberRegisterRequest) {
        memberService.registerMember(memberRegisterRequest);
        return Response.onSuccess();
    }

    @PatchMapping("/info")
    public Response<Void> updateMemberInfo(
            @AuthMember final Member member,
            @RequestBody final MemberRegisterRequest memberRegisterRequest) {
        memberService.updateMemberInfo(memberRegisterRequest, member);
        return Response.onSuccess();
    }

    @PostMapping("/refund-account")
    public Response<Void> registerRefundAccount(
            @AuthMember final Member member,
            @RequestBody final RefundAccountRequest refundAccountRequest) {
        memberService.registerRefundAccount(refundAccountRequest, member);
        return Response.onSuccess();
    }

    @DeleteMapping("/refund-account")
    public Response<Void> deleteRefundAccount(
            @AuthMember final Member member) {
        memberService.deleteRefundAccount(member);
        return Response.onSuccess();
    }
}
