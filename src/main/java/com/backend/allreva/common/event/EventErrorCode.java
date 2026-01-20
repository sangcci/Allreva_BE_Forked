package com.backend.allreva.common.event;

import org.springframework.http.HttpStatus;

import com.backend.allreva.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventErrorCode implements ErrorCode {

    JSON_PARSING_ERROR(HttpStatus.BAD_GATEWAY.value(), "JSON_PARSING_ERROR", "이벤트 json 파싱 오류입니다.");

    private final int status;
    private final String code;
    private final String message;

}
