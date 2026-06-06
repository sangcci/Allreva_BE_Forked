package com.backend.allreva.concert.place.domain;

import com.backend.allreva.common.exception.ErrorCode;

public enum ConcertHallErrorCode implements ErrorCode {
    CONCERT_HALL_NOT_FOUND(404, "CONCERT_HALL_NOT_FOUND", "존재하지 않는 공연장입니다"),
    CONCERT_HALL_SEARCH_NOTFOUND(404, "CONCERT_HALL_SEARCH_NOT_FOUND", "존재하는 공연장이 없습니다");

    private final int status;
    private final String code;
    private final String message;

    ConcertHallErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
