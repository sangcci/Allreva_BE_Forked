package com.backend.allreva.module.review.concert_review.exception;

import org.springframework.http.HttpStatus;

import com.backend.allreva.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {
    REVIEW_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "REVIEW_SAVE_FAILED", "리뷰 저장 실패"),
    REVIEW_IMAGE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "REVIEW_IMAGE_SAVE_FAILED",
            "리뷰 이미지 저장 실패"),
    REVIEW_NOT_FOUND(HttpStatus.NO_CONTENT.value(), "REVIEW_NOT_FOUND", "해당하는 리뷰가 없습니다."),
    NOT_WRITER(HttpStatus.FORBIDDEN.value(), "NOT_WRITER", "리뷰 작성자가 아니여서 수정 불가"),
    DUPLICATE_LIKE(HttpStatus.FORBIDDEN.value(), "DUPLICATED_LIKE", "이미 좋아요를 누른 리뷰 입니다."),
    NOT_LIKE_MEMBER(HttpStatus.FORBIDDEN.value(), "NOT_LIKE_MEMBER", "좋아요를 누른 멤버가 아닙니다."),
    REVIEW_IMAGE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "REVIEW_IMAGE_DELETE_FAILED",
            "리뷰 이미지 삭제 실패");

    private final int status;
    private final String code;
    private final String message;
}
