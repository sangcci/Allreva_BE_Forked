package com.backend.allreva.module.member.exception;

import org.springframework.http.HttpStatus;

import com.backend.allreva.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "MEMBER_NOT_FOUND", "존재하지 않는 회원입니다.");

    private final int status;
    private final String code;
    private final String message;
}
