package com.backend.allreva.auth.domain;

import com.backend.allreva.common.exception.ErrorCode;

public enum JwtErrorCode implements ErrorCode {
    TOKEN_EMPTY(401, "TOKEN_EMPTY", "토큰이 비어있습니다."),
    TOKEN_INVALID(401, "TOKEN_INVALID", "유효하지 않은 토큰입니다."),
    TOKEN_NOT_FOUND(401, "TOKEN_NOT_FOUND", "토큰을 찾을 수 없습니다."),
    TOKEN_NOT_MATCH(401, "TOKEN_NOT_MATCH", "서버에 저장된 토큰과 일치하지 않습니다.");

    private final int status;
    private final String code;
    private final String message;

    JwtErrorCode(final int status, final String code, final String message) {
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
