package com.backend.allreva.seat_review.command.application;

import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.seat_review.command.application.dto.SeatReviewLikeRequest;
import com.backend.allreva.seat_review.command.domain.SeatReviewLike;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.seat_review.exception.SeatReviewErrorCode;
import com.backend.allreva.seat_review.infra.SeatReviewLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatReviewLikeService {
    private final SeatReviewLikeRepository seatReviewLikeRepository;

    public Long increaseSeatReviewLike(
            final SeatReviewLikeRequest request,
            final Member member) {
        if (checkMemberLike(request.seatReviewId(), member.getId())) {
            throw new CustomException(SeatReviewErrorCode.DUPLICATE_LIKE);
        }

        SeatReviewLike seatReviewLike = seatReviewLikeRepository.save(
                SeatReviewLike.builder()
                        .reviewId(request.seatReviewId())
                        .memberId(member.getId())
                        .build());

        return seatReviewLike.getId();
    }

    // BUG: 좋아요 취소 시 로직 문제 발생
    public void cancelSeatReviewLike(
            final Long seatReviewId,
            final Member member) {
        System.out.println(member.getId());
        if (checkMemberLike(seatReviewId, member.getId())) {
            throw new CustomException(SeatReviewErrorCode.NOT_LIKE_MEMBER);
        }

        seatReviewLikeRepository.deleteById(seatReviewId);
    }

    private boolean checkMemberLike(
            final Long seatReviewId,
            final Long memberId) {
        return seatReviewLikeRepository.existsByReviewIdAndMemberId(seatReviewId, memberId);
    }
}
