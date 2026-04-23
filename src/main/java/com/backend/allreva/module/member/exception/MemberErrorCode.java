package com.backend.allreva.module.member.exception;

import com.backend.allreva.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "MEMBER_NOT_FOUND", "존재하지 않는 회원입니다."),
    DUPLICATE_OAUTH_MEMBER(HttpStatus.CONFLICT.value(), "DUPLICATE_OAUTH_MEMBER", "이미 가입된 소셜 계정입니다.");

    private final int status;
    private final String code;
    private final String message;
}
