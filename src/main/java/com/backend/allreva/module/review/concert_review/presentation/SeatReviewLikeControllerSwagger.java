package com.backend.allreva.module.review.concert_review.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.review.concert_review.application.dto.ReviewLikeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "좌석리뷰 좋아요 API", description = "좌석리뷰 좋아요 API")
public interface SeatReviewLikeControllerSwagger {

    @Operation(summary = "좌석리뷰 좋아요 API", description = "좌석리뷰 좋아요 누르는 API")
    Response<Long> likeSeatReview(ReviewLikeRequest request, Member member);

    @Operation(summary = "좌석리뷰 좋아요 취소 API", description = "좌석리뷰 좋아요 취소 API")
    Response<Void> likeSeatReviewCancel(Long seatReviewLikeId, Member member);
}
