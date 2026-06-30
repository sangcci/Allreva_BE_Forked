package com.backend.allreva.member.fixture;

import static org.instancio.Select.field;

import com.backend.allreva.common.model.Email;
import com.backend.allreva.member.domain.LoginProvider;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.member.domain.MemberInfo;
import com.backend.allreva.member.domain.MemberRole;
import com.backend.allreva.member.domain.MemberStatus;
import com.backend.allreva.member.domain.RefundAccount;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.instancio.Instancio;
import org.instancio.Model;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberFixture {

    public static final String EMAIL = "member@example.com";
    public static final String NICKNAME = "닉네임";
    public static final LoginProvider PROVIDER = LoginProvider.KAKAO;

    public static Model<Member> memberModel() {
        return Instancio.of(Member.class)
                .ignore(field(Member.class, "id"))
                .set(field(Member.class, "email"), new Email(EMAIL))
                .set(field(Member.class, "memberRole"), MemberRole.USER)
                .set(field(Member.class, "memberStatus"), MemberStatus.ACTIVE)
                .set(field(Member.class, "loginProvider"), PROVIDER)
                .set(field(MemberInfo.class, "nickname"), NICKNAME)
                .set(field(MemberInfo.class, "introduce"), "소개")
                .set(field(MemberInfo.class, "profileImageUrl"), "profile.jpg")
                .set(field(RefundAccount.class, "bank"), "은행")
                .set(field(RefundAccount.class, "number"), "123-456")
                .toModel();
    }

    public static Member createMember() {
        return Instancio.create(memberModel());
    }
}
