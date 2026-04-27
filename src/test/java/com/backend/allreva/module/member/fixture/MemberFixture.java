package com.backend.allreva.module.member.fixture;

import static org.instancio.Select.field;

import com.backend.allreva.common.model.BaseEntity;
import com.backend.allreva.common.model.Email;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.member.domain.value.LoginProvider;
import com.backend.allreva.module.member.domain.value.MemberRole;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.instancio.Instancio;
import org.instancio.Model;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberFixture {

    public static final String EMAIL = "member@example.com";
    public static final String OTHER_EMAIL = "other@example.com";
    public static final LoginProvider PROVIDER = LoginProvider.GOOGLE;

    public static Model<Member> memberModel() {
        return Instancio.of(Member.class)
                .ignore(field(Member.class, "id"))
                .ignore(field(BaseEntity.class, "deletedAt"))
                .set(field(Member.class, "memberRole"), MemberRole.USER)
                .set(field(Member.class, "loginProvider"), LoginProvider.GOOGLE)
                .toModel();
    }

    public static Member createTestMember() {
        return Instancio.of(memberModel())
                .set(field(Email.class, "email"), EMAIL)
                .create();
    }

    public static Member createOtherTestMember() {
        return Instancio.of(memberModel())
                .set(field(Email.class, "email"), OTHER_EMAIL)
                .create();
    }

    public static Member createTestMemberWithIndex(final int index) {
        return Instancio.of(memberModel())
                .set(field(Email.class, "email"), "member" + index + "@example.com")
                .create();
    }

    public static Member createTestMember(final String email, final LoginProvider loginProvider) {
        return Instancio.of(memberModel())
                .set(field(Email.class, "email"), email)
                .set(field(Member.class, "loginProvider"), loginProvider)
                .create();
    }
}
