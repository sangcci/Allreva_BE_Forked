package com.backend.allreva.module.concert.review.application.dto;

import jakarta.validation.constraints.NotNull;

public record ReviewLikeRequest(
        @NotNull(message = "reviewId는 필수 입니다")
        Long reviewId
) {
}
