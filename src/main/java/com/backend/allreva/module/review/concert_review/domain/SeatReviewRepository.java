package com.backend.allreva.module.review.concert_review.domain;

import java.util.List;
import java.util.Optional;

import com.backend.allreva.module.review.concert_review.application.dto.SeatReviewResponse;
import com.backend.allreva.module.review.concert_review.application.dto.SeatReviewSearchCondition;

public interface SeatReviewRepository {

    SeatReview save(SeatReview seatReview);

    Optional<SeatReview> findById(Long id);

    void delete(SeatReview seatReview);

    List<SeatReviewResponse> findReviewsWithNoOffset(SeatReviewSearchCondition condition, Long currentMemberId);
}
