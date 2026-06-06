package com.backend.allreva.member.domain;

import com.backend.allreva.common.exception.ErrorCode;

public enum MemberErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND(404, "MEMBER_NOT_FOUND", "존재하지 않는 회원입니다."),
    DUPLICATE_MEMBER_ACCOUNT(409, "DUPLICATE_MEMBER_ACCOUNT", "이미 가입된 계정입니다.");

    private final int status;
    private final String code;
    private final String message;

    MemberErrorCode(final int status, final String code, final String message) {
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
