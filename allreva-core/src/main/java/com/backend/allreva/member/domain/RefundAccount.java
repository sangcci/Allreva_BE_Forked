package com.backend.allreva.member.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RefundAccount {

    private String bank;
    private String number;

    @Builder
    private RefundAccount(final String bank, final String number) {
        this.bank = bank;
        this.number = number;
    }
}
