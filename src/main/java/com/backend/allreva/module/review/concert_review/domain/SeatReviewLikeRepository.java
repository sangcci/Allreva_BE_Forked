package com.backend.allreva.module.review.concert_review.domain;

import java.util.Optional;

public interface SeatReviewLikeRepository {

    SeatReviewLike save(SeatReviewLike seatReviewLike);

    Optional<SeatReviewLike> findByReviewIdAndMemberId(Long reviewId, Long memberId);

    void delete(SeatReviewLike seatReviewLike);

    boolean existsByReviewIdAndMemberId(Long seatReviewId, Long memberId);
}
