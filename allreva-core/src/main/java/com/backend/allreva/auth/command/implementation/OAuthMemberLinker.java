package com.backend.allreva.auth.command.implementation;

import com.backend.allreva.common.model.Email;
import com.backend.allreva.member.command.application.MemberService;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.member.domain.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuthMemberLinker {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    public Member link(final OAuthMember oAuthMember) {
        return findMember(oAuthMember).orElseGet(() -> register(oAuthMember));
    }

    private Optional<Member> findMember(final OAuthMember oAuthMember) {
        Email email = Email.builder().email(oAuthMember.email()).build();
        return memberRepository.findByEmailAndLoginProvider(email, oAuthMember.loginProvider());
    }

    private Member register(final OAuthMember oAuthMember) {
        return memberService.registerByOAuth(
                oAuthMember.email(), oAuthMember.loginProvider(), oAuthMember.profileImageUrl());
    }
}
