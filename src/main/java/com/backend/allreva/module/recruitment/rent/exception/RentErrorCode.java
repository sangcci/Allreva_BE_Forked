package com.backend.allreva.module.recruitment.rent.exception;

import com.backend.allreva.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RentErrorCode implements ErrorCode {
    RENT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "RENT_NOT_FOUND", "존재하지 않는 차 대절 폼입니다."),
    RENT_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "RENT_ACCESS_DENIED", "차 대절 폼에 접근할 수 없습니다."),
    RENT_JOIN_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "RENT_JOIN_NOT_FOUND", "존재하지 않는 차 대절 참석 폼입니다."),
    RENT_JOIN_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "RENT_JOIN_ACCESS_DENIED", "차 대절 참석 폼에 접근할 수 없습니다."),
    RENT_JOIN_ALREADY_EXISTS(HttpStatus.BAD_REQUEST.value(), "RENT_JOIN_ALREADY_EXISTS", "이미 해당 차 대절 폼에 참석했습니다."),
    PASSENGERS_MAXIMUM_REACHED(HttpStatus.BAD_REQUEST.value(), "PASSENGERS_MAXIMUM_REACHED", "차 대절 폼의 최대 인원을 초과했습니다."),
    SLOT_FULL(HttpStatus.CONFLICT.value(), "RENT_SLOT_FULL", "탑승 슬롯이 가득 찼습니다.");

    private final int status;
    private final String code;
    private final String message;
}
