package com.backend.allreva.module.recruitment.rent.application.dto;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record RentJoinCountResponse(
        int rentUpCount, int rentDownCount, int rentRoundCount, int refundCount, int additionalDepositCount) {
    public static final RentJoinCountResponse EMPTY = RentJoinCountResponse.builder()
            .rentUpCount(0)
            .rentDownCount(0)
            .rentRoundCount(0)
            .refundCount(0)
            .additionalDepositCount(0)
            .build();
}
