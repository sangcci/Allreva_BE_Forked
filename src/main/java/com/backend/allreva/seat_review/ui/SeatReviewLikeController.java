package com.backend.allreva.seat_review.ui;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.auth.security.AuthMember;
import com.backend.allreva.seat_review.command.application.SeatReviewLikeService;
import com.backend.allreva.seat_review.command.application.dto.SeatReviewLikeRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/seat-review-like")
public class SeatReviewLikeController implements SeatReviewLikeControllerSwagger {

    private final SeatReviewLikeService seatReviewLikeService;

    @Override
    @PostMapping
    public Response<Long> likeSeatReview(
            @RequestBody @Valid final SeatReviewLikeRequest request,
            @AuthMember final Member member) {
        return Response.onSuccess(
                seatReviewLikeService.increaseSeatReviewLike(request, member));
    }

    @Override
    @DeleteMapping
    public Response<Void> likeSeatReviewCancel(
            @RequestParam(required = true) final Long seatReviewLikeId,
            @AuthMember final Member member) {
        seatReviewLikeService.cancelSeatReviewLike(seatReviewLikeId, member);
        return Response.onSuccess();
    }
}
