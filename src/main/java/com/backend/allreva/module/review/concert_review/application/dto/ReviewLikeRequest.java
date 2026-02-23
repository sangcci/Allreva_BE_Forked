package com.backend.allreva.module.review.concert_review.application.dto;

import jakarta.validation.constraints.NotNull;

public record ReviewLikeRequest(
        @NotNull(message = "reviewId는 필수 입니다")
        Long reviewId
) {
}
