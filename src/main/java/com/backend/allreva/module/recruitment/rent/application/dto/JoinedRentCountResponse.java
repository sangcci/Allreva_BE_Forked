package com.backend.allreva.module.recruitment.rent.application.dto;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record JoinedRentCountResponse(
        int rentUpCount, int rentDownCount, int rentRoundCount, int refundCount, int additionalDepositCount) {
    public static final JoinedRentCountResponse EMPTY = JoinedRentCountResponse.builder()
            .rentUpCount(0)
            .rentDownCount(0)
            .rentRoundCount(0)
            .refundCount(0)
            .additionalDepositCount(0)
            .build();
}
