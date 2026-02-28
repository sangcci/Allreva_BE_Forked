package com.backend.allreva.module.concert.place.exception;

import com.backend.allreva.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ConcertHallErrorCode implements ErrorCode {
    CONCERT_HALL_SEARCH_NOTFOUND(HttpStatus.NOT_FOUND.value(), "CONCERT_HALL_SEARCH_NOT_FOUND", "존재하는 공연장이 없습니다"),
    RELATED_CONCERT_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR.value(), "RELATED_CONCERT_EXCEPTION", "연관 콘서트 에러");

    private final int status;
    private final String code;
    private final String message;
}
