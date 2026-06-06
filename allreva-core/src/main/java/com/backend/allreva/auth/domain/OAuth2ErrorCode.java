package com.backend.allreva.auth.domain;

import com.backend.allreva.common.exception.ErrorCode;

public enum OAuth2ErrorCode implements ErrorCode {
    UNSUPPORTED_PROVIDER(400, "UNSUPPORTED_PROVIDER", "지원하지 않는 provider 입니다.");

    private final int status;
    private final String code;
    private final String message;

    OAuth2ErrorCode(final int status, final String code, final String message) {
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
