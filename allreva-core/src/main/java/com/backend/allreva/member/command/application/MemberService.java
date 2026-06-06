package com.backend.allreva.member.command.application;

import com.backend.allreva.member.command.implementation.MemberInfoUpdater;
import com.backend.allreva.member.command.implementation.MemberReader;
import com.backend.allreva.member.command.implementation.MemberRegister;
import com.backend.allreva.member.command.implementation.MemberRegisterValidator;
import com.backend.allreva.member.command.implementation.MemberWriter;
import com.backend.allreva.member.command.implementation.RefundAccountUpdater;
import com.backend.allreva.member.command.input.MemberInfoUpdateCommand;
import com.backend.allreva.member.domain.LoginProvider;
import com.backend.allreva.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRegister memberRegister;
    private final MemberRegisterValidator memberRegisterValidator;
    private final MemberReader memberReader;
    private final MemberWriter memberWriter;
    private final MemberInfoUpdater memberInfoUpdater;
    private final RefundAccountUpdater refundAccountUpdater;

    @Transactional
    public Member registerByOAuth(final String email, final LoginProvider loginProvider, final String profileImageUrl) {
        memberRegisterValidator.validateNotRegistered(email, loginProvider);
        Member member = memberRegister.registerByOAuth(email, loginProvider, profileImageUrl);
        return memberWriter.save(member);
    }

    @Transactional
    public void registerMember(
            final String email,
            final String nickname,
            final String introduce,
            final LoginProvider loginProvider,
            final String profileImageUrl) {
        memberRegisterValidator.validateNotRegistered(email, loginProvider);
        Member member = memberRegister.register(email, nickname, introduce, loginProvider, profileImageUrl);
        memberWriter.save(member);
    }

    @Transactional
    public void updateMemberInfo(final MemberInfoUpdateCommand command, final Long memberId) {
        Member member = memberReader.getById(memberId);
        memberInfoUpdater.update(
                member, command.nickname(), command.introduce(), command.image().getUrl());
        memberWriter.save(member);
    }

    @Transactional
    public void updateRefundAccount(final String bank, final String number, final Long memberId) {
        Member member = memberReader.getById(memberId);
        refundAccountUpdater.update(member, bank, number);
        memberWriter.save(member);
    }

    @Transactional
    public void resetRefundAccount(final Long memberId) {
        Member member = memberReader.getById(memberId);
        refundAccountUpdater.reset(member);
        memberWriter.save(member);
    }
}
