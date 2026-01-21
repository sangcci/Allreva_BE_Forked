package com.backend.allreva.module.auth.exception;

import com.backend.allreva.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OAuth2ErrorCode implements ErrorCode {

    UNSUPPORTED_PROVIDER(HttpStatus.BAD_REQUEST.value(), "UNSUPPORTED_PROVIDER", "지원하지 않는 provider 입니다."),
    INVALID_REDIRECT_URI(HttpStatus.BAD_REQUEST.value(), "INVALID_REDIRECT_URI", "유효하지 않은 redirect uri 입니다.");

    private final int status;
    private final String code;
    private final String message;
}
