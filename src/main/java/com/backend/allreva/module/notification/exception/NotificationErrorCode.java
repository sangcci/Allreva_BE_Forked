package com.backend.allreva.module.notification.exception;

import com.backend.allreva.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "NOTIFICATION_NOT_FOUND", "존재하지 않는 알람입니다."),
    FCM_TOKEN_GENERATION_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), "FCM_TOKEN_GENERATION_FAILED", "FCM 인증 토큰 발급에 실패했습니다."),
    FCM_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "FCM_SEND_FAILED", "FCM 메시지 전송에 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;
}
