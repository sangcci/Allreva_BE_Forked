package com.backend.allreva.module.concert.review.infra;

import com.backend.allreva.module.concert.review.application.dto.SeatReviewResponse;
import com.backend.allreva.module.concert.review.presentation.SeatReviewSearchCondition;

import java.util.List;

public interface SeatReviewRepositoryCustom {
    List<SeatReviewResponse> findReviewsWithNoOffset(SeatReviewSearchCondition condition, Long currentMemberId);
}
