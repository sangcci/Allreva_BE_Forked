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

    public static final String EMAIL = "member@example.com";
    public static final String OTHER_EMAIL = "other@example.com";
    public static final LoginProvider PROVIDER = LoginProvider.GOOGLE;

    public static Member createTestMember() {
        return createTestMember(EMAIL, PROVIDER);
    }

    public static Member createOtherTestMember() {
        return createTestMember(OTHER_EMAIL, PROVIDER);
    }

    public static Member createTestMemberWithIndex(final int index) {
        return createTestMember("member" + index + "@example.com", PROVIDER);
    }

    public static Member createTestMember(final String email, final LoginProvider loginProvider) {
        return Member.builder()
                .email(new Email(email))
                .memberRole(MemberRole.USER)
                .loginProvider(loginProvider)
                .nickname("JohnDoe")
                .introduce("Hello, I'm John.")
                .profileImageUrl("http://example.com/profile.jpg")
                .build();
    }

    public static Member createMember(final Long memberId, final MemberRole memberRole) {
        var member = Member.builder()
                .email(Email.builder().email(EMAIL).build())
                .nickname("testNickname")
                .memberRole(memberRole)
                .loginProvider(PROVIDER)
                .introduce("introduce")
                .profileImageUrl("https://example.com/profile.jpg")
                .build();
        ReflectionTestUtils.setField(member, "id", memberId);
        return member;
    }
}
