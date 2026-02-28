package com.backend.allreva.module.review.concert_review.infra.jpa;

import com.backend.allreva.module.review.concert_review.domain.SeatReviewLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatReviewLikeJpaRepository extends JpaRepository<SeatReviewLike, Long> {

    boolean existsByReviewIdAndMemberId(Long seatReviewId, Long memberId);

    Optional<SeatReviewLike> findByReviewIdAndMemberId(Long reviewId, Long memberId);
}
