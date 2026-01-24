package com.backend.allreva.seat_review.ui;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.auth.security.AuthMember;
import com.backend.allreva.seat_review.command.application.SeatReviewFacade;
import com.backend.allreva.seat_review.command.application.dto.ReviewCreateRequest;
import com.backend.allreva.seat_review.command.application.dto.ReviewUpdateRequest;
import com.backend.allreva.seat_review.query.application.SeatReviewQueryService;
import com.backend.allreva.seat_review.query.application.dto.SeatReviewResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/seat-review")
@RequiredArgsConstructor
@Slf4j
public class SeatReviewController implements SeatReviewControllerSwagger {

    private final SeatReviewFacade seatReviewFacade;
    private final SeatReviewQueryService seatReviewQueryService;

    @Override
    @PostMapping
    public Response<Long> createSeatReview(
            @RequestBody @Valid final ReviewCreateRequest request,
            @AuthMember final Member member) {
        return Response.onSuccess(
                seatReviewFacade.createSeatReview(request, member));
    }

    @Override
    @PatchMapping
    public Response<Long> updateSeatReview(
            @RequestBody @Valid final ReviewUpdateRequest request,
            @AuthMember final Member member) {
        return Response.onSuccess(
                seatReviewFacade.updateSeatReview(request, member));
    }

    @Override
    @DeleteMapping
    public Response<Void> deleteSeatReview(
            @RequestParam final Long seatReviewId,
            @AuthMember final Member member) {
        seatReviewFacade.deleteSeatReview(seatReviewId, member);

        return Response.onSuccess();
    }

    @Override
    @GetMapping
    public Response<List<SeatReviewResponse>> getReviews(
            @RequestParam(required = false) final Long lastId,
            @RequestParam(required = false) final LocalDateTime lastCreatedAt,
            @RequestParam(defaultValue = "20") final int size,
            @RequestParam(defaultValue = "CREATED_DESC") final SortType sortType,
            @RequestParam final String hallId,
            @AuthMember final Member member) {
        SeatReviewSearchCondition condition = new SeatReviewSearchCondition(lastId, lastCreatedAt, size, sortType,
                hallId, member.getId());
        List<SeatReviewResponse> reviews = seatReviewQueryService.getReviews(condition, member.getId());
        return Response.onSuccess(reviews);
    }

}
