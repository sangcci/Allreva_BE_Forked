package com.backend.allreva.module.review.concert_review.fixture;

import com.backend.allreva.module.review.concert_review.domain.SeatReview;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SeatReviewFixture {

    public static SeatReview createSeatReview(Long reviewId, Long memberId, String hallId) {
        SeatReview review = SeatReview.builder()
                .seat("1층 R석 3열 15번")
                .content("무대가 잘 보이고 음향도 좋았습니다.")
                .star(5)
                .memberId(memberId)
                .hallId(hallId)
                .viewDate(LocalDate.of(2024, 12, 15))
                .concertTitle("아이유 콘서트")
                .build();

        ReflectionTestUtils.setField(review, "id", reviewId);
        return review;
    }

    public static SeatReview createSeatReviewWithStar(Long reviewId, Long memberId, String hallId, int star) {
        SeatReview review = SeatReview.builder()
                .seat("2층 S석 5열 10번")
                .content("별점 " + star + "점 리뷰입니다.")
                .star(star)
                .memberId(memberId)
                .hallId(hallId)
                .viewDate(LocalDate.of(2024, 12, 20))
                .concertTitle("아이유 콘서트")
                .build();

        ReflectionTestUtils.setField(review, "id", reviewId);
        return review;
    }

    public static SeatReview createDetailedSeatReview(
            Long reviewId,
            Long memberId,
            String hallId,
            String seat,
            String content,
            int star) {
        SeatReview review = SeatReview.builder()
                .seat(seat)
                .content(content)
                .star(star)
                .memberId(memberId)
                .hallId(hallId)
                .viewDate(LocalDate.now())
                .concertTitle("테스트 콘서트")
                .build();

        ReflectionTestUtils.setField(review, "id", reviewId);
        return review;
    }
}
