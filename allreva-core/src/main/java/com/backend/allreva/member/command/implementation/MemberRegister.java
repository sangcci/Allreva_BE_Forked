package com.backend.allreva.member.command.implementation;

import com.backend.allreva.common.model.Email;
import com.backend.allreva.member.domain.LoginProvider;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.member.domain.MemberRole;
import com.backend.allreva.member.domain.MemberStatus;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberRegister {

    public Member register(
            final String email,
            final String nickname,
            final String introduce,
            final LoginProvider loginProvider,
            final String profileImageUrl) {
        Member member = Member.builder()
                .email(Email.builder().email(email).build())
                .nickname(nickname)
                .memberRole(MemberRole.USER)
                .memberStatus(MemberStatus.ACTIVE)
                .introduce(introduce)
                .profileImageUrl(profileImageUrl)
                .loginProvider(loginProvider)
                .build();
        member.resetRefundAccount();
        return member;
    }

    public Member registerByOAuth(final String email, final LoginProvider loginProvider, final String profileImageUrl) {
        Member member = Member.builder()
                .email(Email.builder().email(email).build())
                .nickname(generateUniqueNickname())
                .memberRole(MemberRole.USER)
                .memberStatus(MemberStatus.REGISTERED)
                .introduce("")
                .profileImageUrl(profileImageUrl)
                .loginProvider(loginProvider)
                .build();
        member.resetRefundAccount();
        return member;
    }

    private String generateUniqueNickname() {
        return "user-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}
