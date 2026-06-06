package com.backend.allreva.recruitment.rent.domain;

import com.backend.allreva.common.exception.ErrorCode;

public enum RentErrorCode implements ErrorCode {
    RENT_NOT_FOUND(404, "RENT_NOT_FOUND", "존재하지 않는 차 대절 폼입니다."),
    RENT_ACCESS_DENIED(403, "RENT_ACCESS_DENIED", "차 대절 폼에 접근할 수 없습니다."),
    RENT_JOIN_NOT_FOUND(404, "RENT_JOIN_NOT_FOUND", "존재하지 않는 차 대절 참석 폼입니다."),
    RENT_JOIN_ACCESS_DENIED(403, "RENT_JOIN_ACCESS_DENIED", "차 대절 참석 폼에 접근할 수 없습니다."),
    RENT_PARTICIPANT_ACCESS_DENIED(403, "RENT_PARTICIPANT_ACCESS_DENIED", "차 대절 참여자 폼에 접근할 수 없습니다."),
    RENT_JOIN_ALREADY_EXISTS(400, "RENT_JOIN_ALREADY_EXISTS", "이미 해당 차 대절 폼에 참석했습니다."),
    RENT_HOST_CANNOT_JOIN(400, "RENT_HOST_CANNOT_JOIN", "주최자는 본인이 등록한 차 대절 폼에 참석할 수 없습니다."),
    PASSENGERS_MAXIMUM_REACHED(400, "PASSENGERS_MAXIMUM_REACHED", "차 대절 폼의 최대 인원을 초과했습니다."),
    SLOT_FULL(409, "RENT_SLOT_FULL", "탑승 슬롯이 가득 찼습니다.");

    private final int status;
    private final String code;
    private final String message;

    RentErrorCode(final int status, final String code, final String message) {
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
