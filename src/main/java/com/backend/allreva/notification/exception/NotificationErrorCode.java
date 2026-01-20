package com.backend.allreva.notification.exception;

import org.springframework.http.HttpStatus;

import com.backend.allreva.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {

    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "NOTIFICATION_NOT_FOUND", "존재하지 않는 알람입니다.");

    private final int status;
    private final String code;
    private final String message;
}
