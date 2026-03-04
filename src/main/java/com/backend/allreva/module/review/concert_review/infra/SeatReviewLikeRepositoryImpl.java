package com.backend.allreva.module.review.concert_review.infra;

import com.backend.allreva.module.review.concert_review.domain.SeatReviewLike;
import com.backend.allreva.module.review.concert_review.domain.SeatReviewLikeRepository;
import com.backend.allreva.module.review.concert_review.infra.jpa.SeatReviewLikeJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SeatReviewLikeRepositoryImpl implements SeatReviewLikeRepository {

    private final SeatReviewLikeJpaRepository jpa;

    @Override
    public SeatReviewLike save(final SeatReviewLike seatReviewLike) {
        return jpa.save(seatReviewLike);
    }

    @Override
    public Optional<SeatReviewLike> findByReviewIdAndMemberId(final Long reviewId, final Long memberId) {
        return jpa.findByReviewIdAndMemberId(reviewId, memberId);
    }

    @Override
    public void delete(final SeatReviewLike seatReviewLike) {
        jpa.delete(seatReviewLike);
    }

    @Override
    public boolean existsByReviewIdAndMemberId(final Long seatReviewId, final Long memberId) {
        return jpa.existsByReviewIdAndMemberId(seatReviewId, memberId);
    }
}
