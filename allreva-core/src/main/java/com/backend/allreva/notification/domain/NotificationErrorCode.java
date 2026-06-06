package com.backend.allreva.notification.domain;

import com.backend.allreva.common.exception.ErrorCode;

public enum NotificationErrorCode implements ErrorCode {
    NOTIFICATION_NOT_FOUND(404, "NOTIFICATION_NOT_FOUND", "존재하지 않는 알람입니다."),
    FCM_TOKEN_GENERATION_FAILED(500, "FCM_TOKEN_GENERATION_FAILED", "FCM 인증 토큰 발급에 실패했습니다."),
    FCM_SEND_FAILED(500, "FCM_SEND_FAILED", "FCM 메시지 전송에 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;

    NotificationErrorCode(final int status, final String code, final String message) {
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
