package com.backend.allreva.module.review.concert_review.application;

import com.backend.allreva.module.concert.place.application.HallService;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.review.concert_review.application.dto.ReviewCreateRequest;
import com.backend.allreva.module.review.concert_review.application.dto.ReviewUpdateRequest;
import com.backend.allreva.module.review.concert_review.application.dto.SeatReviewResponse;
import com.backend.allreva.module.review.concert_review.application.dto.SeatReviewSearchCondition;
import com.backend.allreva.module.review.concert_review.domain.SeatReview;
import com.backend.allreva.module.review.concert_review.domain.SeatReviewRepository;

import java.util.List;
import com.backend.allreva.module.review.concert_review.exception.ReviewErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final SeatReviewRepository seatReviewRepository;
    private final HallService hallService;

    // Query methods (read-only)
    public List<SeatReviewResponse> getReviews(
            final SeatReviewSearchCondition condition,
            final Long currentMemberId) {
        return seatReviewRepository.findReviewsWithNoOffset(condition, currentMemberId);
    }

    // Command methods (write)
    @Transactional
    public Long createReview(
            final ReviewCreateRequest request,
            final Member member) {
        SeatReview savedSeatReview = seatReviewRepository.save(SeatReview.builder()
                .seat(request.seat())
                .content(request.content())
                .star(request.star())
                .memberId(member.getId())
                .hallId(request.hallId())
                .viewDate(request.viewDate())
                .concertTitle(request.concertTitle())
                .build());

        hallService.updateConcertHallStar(savedSeatReview.getHallId(), savedSeatReview.getStar(), 1);

        return savedSeatReview.getId();
    }

    @Transactional
    public SeatReview updateReview(
            final ReviewUpdateRequest request,
            final Member member) {
        SeatReview seatReview = seatReviewRepository.findById(request.reviewId())
                .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));
        int starDelta = request.star() - seatReview.getStar();
        validateWriter(seatReview.getMemberId(), member.getId());
        seatReview.updateSeatReview(request);

        hallService.updateConcertHallStar(seatReview.getHallId(), starDelta, 0);

        return seatReviewRepository.save(seatReview);
    }

    @Transactional
    public void deleteReview(final Long id, final Member member) {
        SeatReview seatReview = seatReviewRepository.findById(id)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));
        validateWriter(seatReview.getMemberId(), member.getId());

        hallService.updateConcertHallStar(seatReview.getHallId(), -seatReview.getStar(), -1);

        seatReviewRepository.delete(seatReview);
    }

    private void validateWriter(final Long writerId, final Long memberId) {
        if (!writerId.equals(memberId)) {
            throw new CustomException(ReviewErrorCode.NOT_WRITER);
        }
    }
}
