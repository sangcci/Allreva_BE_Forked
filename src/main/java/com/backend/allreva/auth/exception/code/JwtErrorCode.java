package com.backend.allreva.auth.exception.code;

import com.backend.allreva.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode implements ErrorCode {

    TOKEN_EMPTY(HttpStatus.UNAUTHORIZED.value(), "TOKEN_EMPTY", "토큰이 비어있습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED.value(), "TOKEN_INVALID", "유효하지 않은 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED.value(), "TOKEN_NOT_FOUND", "토큰을 찾을 수 없습니다."),
    TOKEN_NOT_MATCH(HttpStatus.UNAUTHORIZED.value(), "TOKEN_NOT_MATCH", "서버에 저장된 토큰과 일치하지 않습니다.");

    private final int status;
    private final String code;
    private final String message;
}
