package com.backend.allreva.module.concert.review.application;

import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.concert.review.application.dto.ReviewCreateRequest;
import com.backend.allreva.module.concert.review.application.dto.ReviewUpdateRequest;
import com.backend.allreva.module.concert.review.domain.SeatReview;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class ReviewFacade {

    private final ReviewService reviewService;
    private final ReviewImageService reviewImageService;

    public Long createReview(
            final ReviewCreateRequest request,
            final Member member) {
        Long seatReviewId = reviewService.createReview(request, member);

        reviewImageService.saveImageMetadata(seatReviewId, request.imageUrls());

        return seatReviewId;
    }

    public Long updateReview(
            final ReviewUpdateRequest request,
            final Member member) {
        SeatReview seatReview = reviewService.updateReview(request, member);

        // 기존 이미지 삭제
        reviewImageService.deleteImages(request.reviewId());

        // 새로운 이미지 업로드
        reviewImageService.saveImageMetadata(request.reviewId(), request.imageUrls());

        return seatReview.getId();
    }

    public void deleteReview(
            final Long reviewId,
            final Member member) {
        reviewImageService.deleteImages(reviewId);

        reviewService.deleteReview(reviewId, member);
    }
}
