package com.backend.allreva.module.review.concert_review.domain;

import com.backend.allreva.module.review.concert_review.application.dto.SeatReviewResponse;
import com.backend.allreva.module.review.concert_review.application.dto.SeatReviewSearchCondition;
import java.util.List;
import java.util.Optional;

public interface SeatReviewRepository {

    SeatReview save(SeatReview seatReview);

    Optional<SeatReview> findById(Long id);

    void delete(SeatReview seatReview);

    List<SeatReviewResponse> findReviewsWithNoOffset(SeatReviewSearchCondition condition, Long currentMemberId);
}
