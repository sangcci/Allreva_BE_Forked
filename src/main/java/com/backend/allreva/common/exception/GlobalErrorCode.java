package com.backend.allreva.common.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements ErrorCode {
    BAD_REQUEST_ERROR(400, BAD_REQUEST.name(), "잘못된 요청입니다."),
    NOT_SUPPORTED_URI_ERROR(404, NOT_FOUND.name(), "올바르지 않은 URI입니다."),
    NOT_SUPPORTED_METHOD_ERROR(405, METHOD_NOT_ALLOWED.name(), "지원하지 않는 Method입니다."),
    NOT_SUPPORTED_MEDIA_TYPE_ERROR(415, UNSUPPORTED_MEDIA_TYPE.name(), "지원하지 않는 Media type입니다."),
    SERVER_ERROR(500, INTERNAL_SERVER_ERROR.name(), "서버 에러, 관리자에게 문의해주세요."),
    UNAUTHORIZED_ERROR(401, UNAUTHORIZED.name(), "인증되지 않았습니다."),
    ACCESS_DENIED(403, FORBIDDEN.name(), "올바르지 않은 권한입니다."),
    NOT_FOUND_DATA(404, NOT_FOUND.name(), "해당 데이터가 존재하지 않습니다."),
    UPLOAD_FAILED(500, INTERNAL_SERVER_ERROR.name(), "파일 업로드에 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;
}
