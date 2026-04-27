package com.backend.allreva.module.member.fixture;

import static org.instancio.Select.field;

import com.backend.allreva.module.member.application.dto.MemberRegisterRequest;
import com.backend.allreva.module.member.application.dto.RefundAccountRequest;
import com.backend.allreva.module.member.domain.value.LoginProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.instancio.Instancio;
import org.instancio.Model;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberRequestFixture {

    public static Model<MemberRegisterRequest> memberRegisterRequestModel() {
        return Instancio.of(MemberRegisterRequest.class)
                .set(field(MemberRegisterRequest.class, "loginProvider"), LoginProvider.GOOGLE)
                .toModel();
    }

    public static Model<RefundAccountRequest> refundAccountRequestModel() {
        return Instancio.of(RefundAccountRequest.class).toModel();
    }
}
