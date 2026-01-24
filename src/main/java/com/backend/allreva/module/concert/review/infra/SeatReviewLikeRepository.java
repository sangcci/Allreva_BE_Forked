package com.backend.allreva.module.concert.review.infra;

import com.backend.allreva.module.concert.review.domain.SeatReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeatReviewLikeRepository extends JpaRepository<SeatReviewLike, Long> {
    boolean existsByReviewIdAndMemberId(Long seatReviewId, Long memberId);

    // BUGFIX: Added missing method needed for correct like cancellation
    Optional<SeatReviewLike> findByReviewIdAndMemberId(Long reviewId, Long memberId);
}
