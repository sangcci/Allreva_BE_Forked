package com.backend.allreva.module.member.fixture;

import com.backend.allreva.common.model.Email;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.member.domain.value.LoginProvider;
import com.backend.allreva.module.member.domain.value.MemberRole;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.test.util.ReflectionTestUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberFixture {

    public static Member createMember(final Long memberId, final MemberRole memberRole) {
        var member = Member.builder()
                .email(Email.builder().email("test@email.com").build())
                .nickname("testNickname")
                .memberRole(memberRole)
                .loginProvider(LoginProvider.GOOGLE)
                .introduce("introduce")
                .profileImageUrl("https://example.com/profile.jpg")
                .build();
        ReflectionTestUtils.setField(member, "id", memberId);
        return member;
    }

    public static Member createTestMember() {
        return Member.builder()
                .email(new Email("example@example.com"))
                .memberRole(MemberRole.USER)
                .loginProvider(LoginProvider.GOOGLE)
                .nickname("JohnDoe")
                .introduce("Hello, I'm John.")
                .profileImageUrl("http://example.com/profile.jpg")
                .build();
    }
}
