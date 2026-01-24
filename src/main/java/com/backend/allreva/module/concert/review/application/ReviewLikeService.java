package com.backend.allreva.module.concert.review.application;

import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.concert.review.application.dto.ReviewLikeRequest;
import com.backend.allreva.module.concert.review.domain.SeatReviewLike;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.concert.review.exception.ReviewErrorCode;
import com.backend.allreva.module.concert.review.infra.SeatReviewLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewLikeService {
    private final SeatReviewLikeRepository seatReviewLikeRepository;

    public Long increaseReviewLike(
            final ReviewLikeRequest request,
            final Member member) {
        if (checkMemberLike(request.reviewId(), member.getId())) {
            throw new CustomException(ReviewErrorCode.DUPLICATE_LIKE);
        }

        SeatReviewLike seatReviewLike = seatReviewLikeRepository.save(
                SeatReviewLike.builder()
                        .reviewId(request.reviewId())
                        .memberId(member.getId())
                        .build());

        return seatReviewLike.getId();
    }

    /**
     * BUGFIX: Fixed inverted condition and incorrect delete method
     * - Before: if (checkMemberLike) throw error (inverted logic)
     * - After: if (!checkMemberLike) throw error (correct logic)
     * - Before: deleteById(seatReviewId) (deletes wrong entity)
     * - After: find entity by reviewId and memberId, then delete (correct)
     */
    public void cancelReviewLike(
            final Long reviewId,
            final Member member) {
        // FIX 1: Inverted condition - should check if NOT liked
        if (!checkMemberLike(reviewId, member.getId())) {
            throw new CustomException(ReviewErrorCode.NOT_LIKE_MEMBER);
        }

        // FIX 2: Find the actual SeatReviewLike entity and delete it
        SeatReviewLike like = seatReviewLikeRepository
                .findByReviewIdAndMemberId(reviewId, member.getId())
                .orElseThrow(() -> new CustomException(ReviewErrorCode.NOT_LIKE_MEMBER));

        seatReviewLikeRepository.delete(like);
    }

    private boolean checkMemberLike(
            final Long seatReviewId,
            final Long memberId) {
        return seatReviewLikeRepository.existsByReviewIdAndMemberId(seatReviewId, memberId);
    }
}
