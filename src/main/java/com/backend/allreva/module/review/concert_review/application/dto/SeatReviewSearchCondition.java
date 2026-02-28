package com.backend.allreva.module.review.concert_review.application.dto;

import java.time.LocalDateTime;

public record SeatReviewSearchCondition(
        Long lastId, LocalDateTime lastCreatedAt, int size, SortType sortType, String hallId, Long memberId) {}
