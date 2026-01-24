package com.backend.allreva.module.concert.concert.exception;

import org.springframework.http.HttpStatus;

import com.backend.allreva.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConcertErrorCode implements ErrorCode {
    CONCERT_SEARCH_NOT_FOUND(HttpStatus.NO_CONTENT.value(), "CONCERT_SEARCH_NOT_FOUND", "검색어에 존재하는 콘서트가 없습니다."),
    CONCERT_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "CONCERT_STATUS_NOT_FOUND", "존재하지 않는 공연상태입니다."),
    CONCERT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "CONCERT_NOT_FOUND", "존재하지 않는 공연입니다.");

    private final int status;
    private final String code;
    private final String message;

}
