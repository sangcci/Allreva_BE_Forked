package com.backend.allreva.concert.concert.domain;

import com.backend.allreva.common.exception.ErrorCode;

public enum ConcertErrorCode implements ErrorCode {
    CONCERT_SEARCH_NOT_FOUND(204, "CONCERT_SEARCH_NOT_FOUND", "검색어에 존재하는 콘서트가 없습니다."),
    CONCERT_STATUS_NOT_FOUND(404, "CONCERT_STATUS_NOT_FOUND", "존재하지 않는 공연상태입니다."),
    CONCERT_NOT_FOUND(404, "CONCERT_NOT_FOUND", "존재하지 않는 공연입니다.");

    private final int status;
    private final String code;
    private final String message;

    ConcertErrorCode(final int status, final String code, final String message) {
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
