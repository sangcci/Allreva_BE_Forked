package com.backend.allreva.member;

import com.backend.allreva.auth.security.AuthMember;
import com.backend.allreva.common.web.response.View;
import com.backend.allreva.member.command.application.MemberService;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.member.request.MemberInfoUpdateRequest;
import com.backend.allreva.member.request.MemberRegisterRequest;
import com.backend.allreva.member.request.RefundAccountRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/members")
public class MemberCommandController implements MemberCommandControllerSwagger {

    private final MemberService memberCommandService;

    @Override
    @PostMapping("/register")
    public View<Void> registerMember(@Valid @RequestBody final MemberRegisterRequest request) {
        memberCommandService.registerMember(
                request.email(),
                request.nickname(),
                request.introduce(),
                request.loginProvider(),
                request.image().getUrl());
        return View.onSuccess();
    }

    @Override
    @PatchMapping("/info")
    public View<Void> updateMemberInfo(
            @AuthMember final Member member, @Valid @RequestBody final MemberInfoUpdateRequest request) {
        memberCommandService.updateMemberInfo(request.toCommand(), member.getId());
        return View.onSuccess();
    }

    @Override
    @PostMapping("/refund-account")
    public View<Void> updateRefundAccount(
            @AuthMember final Member member, @Valid @RequestBody final RefundAccountRequest request) {
        memberCommandService.updateRefundAccount(request.bank(), request.number(), member.getId());
        return View.onSuccess();
    }

    @Override
    @DeleteMapping("/refund-account")
    public View<Void> resetRefundAccount(@AuthMember final Member member) {
        memberCommandService.resetRefundAccount(member.getId());
        return View.onSuccess();
    }
}
