package com.backend.allreva.module.review.concert_review.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.review.concert_review.application.dto.ReviewCreateRequest;
import com.backend.allreva.module.review.concert_review.application.dto.ReviewUpdateRequest;
import com.backend.allreva.module.review.concert_review.application.dto.SeatReviewResponse;
import com.backend.allreva.module.review.concert_review.application.dto.SeatReviewSearchCondition;
import com.backend.allreva.module.review.concert_review.domain.SeatReview;
import com.backend.allreva.module.review.concert_review.domain.SeatReviewRepository;
import com.backend.allreva.module.review.concert_review.exception.ReviewErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final SeatReviewRepository seatReviewRepository;

    public List<SeatReviewResponse> getReviews(final SeatReviewSearchCondition condition, final Long currentMemberId) {
        return seatReviewRepository.findReviewsWithNoOffset(condition, currentMemberId);
    }

    @Transactional
    public Long createReview(final ReviewCreateRequest request, final Member member) {
        SeatReview savedSeatReview = seatReviewRepository.save(SeatReview.builder()
                .seat(request.seat())
                .content(request.content())
                .star(request.star())
                .memberId(member.getId())
                .hallId(request.hallId())
                .viewDate(request.viewDate())
                .concertTitle(request.concertTitle())
                .build());

        return savedSeatReview.getId();
    }

    @Transactional
    public SeatReview updateReview(final ReviewUpdateRequest request, final Member member) {
        SeatReview seatReview = seatReviewRepository
                .findById(request.reviewId())
                .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));
        validateWriter(seatReview.getMemberId(), member.getId());
        seatReview.updateSeatReview(request);

        return seatReviewRepository.save(seatReview);
    }

    @Transactional
    public void deleteReview(final Long id, final Member member) {
        SeatReview seatReview = seatReviewRepository
                .findById(id)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));
        validateWriter(seatReview.getMemberId(), member.getId());

        seatReviewRepository.delete(seatReview);
    }

    private void validateWriter(final Long writerId, final Long memberId) {
        if (!writerId.equals(memberId)) {
            throw new CustomException(ReviewErrorCode.NOT_WRITER);
        }
    }
}
