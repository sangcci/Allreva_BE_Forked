package com.backend.allreva.member.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RefundAccountRequest(
        @NotBlank String bank, @NotBlank String number) {}
