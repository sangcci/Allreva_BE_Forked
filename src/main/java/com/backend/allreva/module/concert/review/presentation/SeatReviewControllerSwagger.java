package com.backend.allreva.module.concert.review.presentation;

import java.time.LocalDateTime;
import java.util.List;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.concert.review.application.dto.ReviewCreateRequest;
import com.backend.allreva.module.concert.review.application.dto.ReviewUpdateRequest;
import com.backend.allreva.module.concert.review.application.dto.SeatReviewResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "좌석리뷰 API", description = "좌석리뷰 API")
public interface SeatReviewControllerSwagger {

    @Operation(summary = "좌석리뷰 생성 API", description = "좌석리뷰 생성 API")
    Response<Long> createSeatReview(
            ReviewCreateRequest request,
            Member member);

    @Operation(summary = "좌석리뷰 수정 API", description = "좌석리뷰 수정 API")
    Response<Long> updateSeatReview(
            ReviewUpdateRequest request,
            Member member);

    @Operation(summary = "좌석리뷰 삭제 API", description = "좌석리뷰 삭제 API")
    Response<Void> deleteSeatReview(
            Long seatReviewId,
            Member member);

    @Operation(summary = "좌석리뷰 조회 API", description = "좌석리뷰 조회 API")
    Response<List<SeatReviewResponse>> getReviews(
            Long lastId,
            LocalDateTime lastCreatedAt,
            int size,
            SortType sortType,
            String hallId,
            Member member);
}
