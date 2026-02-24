package com.backend.allreva.module.recruitment.rent.application.dto;

import com.backend.allreva.module.recruitment.rent.domain.Rent;

public record DepositAccountResponse(String depositAccount) {
    public static DepositAccountResponse from(Rent rent) {
        return new DepositAccountResponse(rent.getDepositAccount());
    }
}
