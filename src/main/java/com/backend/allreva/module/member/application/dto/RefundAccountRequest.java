package com.backend.allreva.module.member.application.dto;

import lombok.Builder;

@Builder
public record RefundAccountRequest(
        String bank,
        String number
) {

}
