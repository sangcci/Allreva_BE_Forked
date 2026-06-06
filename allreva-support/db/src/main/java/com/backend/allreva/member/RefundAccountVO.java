package com.backend.allreva.member;

import com.backend.allreva.member.domain.RefundAccount;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefundAccountVO {

    private String bank;
    private String number;

    RefundAccountVO(final String bank, final String number) {
        this.bank = bank;
        this.number = number;
    }

    public static RefundAccountVO from(final RefundAccount refundAccount) {
        return refundAccount == null ? null : new RefundAccountVO(refundAccount.getBank(), refundAccount.getNumber());
    }

    public RefundAccount toDomain() {
        return RefundAccount.builder().bank(bank).number(number).build();
    }
}
