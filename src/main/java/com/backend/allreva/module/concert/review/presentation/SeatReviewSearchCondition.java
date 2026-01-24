package com.backend.allreva.module.concert.review.presentation;

import java.time.LocalDateTime;

public record SeatReviewSearchCondition(
        Long lastId,
        LocalDateTime lastCreatedAt,
        int size,
        SortType sortType,
        String hallId,
        Long memberId
) {}