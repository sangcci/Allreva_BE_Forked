package com.backend.allreva.module.concert.diary.exception;

import org.springframework.http.HttpStatus;

import com.backend.allreva.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DiaryErrorCode implements ErrorCode {

    DIARY_NOT_WRITER(HttpStatus.FORBIDDEN.value(), "DIARY_NOT_WRITER", "삭제할 권한이 없습니다"),
    DIARY_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "DIARY_NOT_FOUND", "존재하지 않는 공연 기록입니다");

    private final int status;
    private final String code;
    private final String message;

}
