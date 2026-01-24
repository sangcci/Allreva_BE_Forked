package com.backend.allreva.module.concert.review.application;

import com.backend.allreva.module.concert.hall.application.HallService;
import com.backend.allreva.module.concert.hall.domain.ConcertHall;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.concert.hall.exception.ConcertHallErrorCode;
import com.backend.allreva.module.search.domain.ConcertHallSearchRepository;
import com.backend.allreva.module.concert.hall.domain.ConcertHallDocument;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.concert.review.application.dto.ReviewCreateRequest;
import com.backend.allreva.module.concert.review.application.dto.ReviewUpdateRequest;
import com.backend.allreva.module.concert.review.application.dto.SeatReviewResponse;
import com.backend.allreva.module.concert.review.presentation.SeatReviewSearchCondition;
import com.backend.allreva.module.concert.review.domain.SeatReview;

import java.util.List;
import com.backend.allreva.module.concert.review.exception.ReviewErrorCode;
import com.backend.allreva.module.concert.review.infra.SeatReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final SeatReviewRepository seatReviewRepository;
    private final HallService hallService;
    private final ConcertHallSearchRepository concertHallSearchRepository;

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
        try {
            SeatReview savedSeatReview = seatReviewRepository.save(SeatReview.builder()
                    .seat(request.seat())
                    .content(request.content())
                    .star(request.star())
                    .memberId(member.getId())
                    .hallId(request.hallId())
                    .viewDate(request.viewDate())
                    .concertTitle(request.concertTitle())
                    .build());

            ConcertHall concertHall = hallService.updateConcertHallStar(savedSeatReview.getHallId(),
                    savedSeatReview.getStar(), 1);

            ConcertHallDocument concertHallDocument = concertHallSearchRepository.findById(concertHall.getId())
                    .orElseThrow(() -> new CustomException(ConcertHallErrorCode.CONCERT_HALL_SEARCH_NOTFOUND));
            concertHallDocument.updateStar(concertHall.getStar());
            concertHallSearchRepository.save(concertHallDocument);

            return savedSeatReview.getId();
        } catch (Exception e) {
            throw new CustomException(ReviewErrorCode.REVIEW_SAVE_FAILED);
        }
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

        ConcertHall concertHall = hallService.updateConcertHallStar(seatReview.getHallId(), starDelta, 0);

        ConcertHallDocument concertHallDocument = concertHallSearchRepository.findById(concertHall.getId())
                .orElseThrow(() -> new CustomException(ConcertHallErrorCode.CONCERT_HALL_SEARCH_NOTFOUND));

        concertHallDocument.updateStar(concertHall.getStar());
        concertHallSearchRepository.save(concertHallDocument);

        return seatReviewRepository.save(seatReview);
    }

    @Transactional
    public void deleteReview(final Long id, final Member member) {
        SeatReview seatReview = seatReviewRepository.findById(id)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));
        validateWriter(seatReview.getMemberId(), member.getId());

        ConcertHall concertHall = hallService.updateConcertHallStar(seatReview.getHallId(),
                -seatReview.getStar(), -1);

        ConcertHallDocument concertHallDocument = concertHallSearchRepository.findById(concertHall.getId())
                .orElseThrow(() -> new CustomException(ConcertHallErrorCode.CONCERT_HALL_SEARCH_NOTFOUND));
        concertHallDocument.updateStar(concertHall.getStar());
        concertHallSearchRepository.save(concertHallDocument);

        seatReviewRepository.delete(seatReview);
    }

    private void validateWriter(final Long writerId, final Long memberId) {
        if (!writerId.equals(memberId)) {
            throw new CustomException(ReviewErrorCode.NOT_WRITER);
        }
    }
}
