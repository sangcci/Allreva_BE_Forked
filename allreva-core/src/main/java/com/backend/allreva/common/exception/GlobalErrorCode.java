package com.backend.allreva.common.exception;

public enum GlobalErrorCode implements ErrorCode {
    BAD_REQUEST_ERROR(400, "BAD_REQUEST", "잘못된 요청입니다."),
    NOT_SUPPORTED_URI_ERROR(404, "NOT_FOUND", "올바르지 않은 URI입니다."),
    NOT_SUPPORTED_METHOD_ERROR(405, "METHOD_NOT_ALLOWED", "지원하지 않는 Method입니다."),
    NOT_SUPPORTED_MEDIA_TYPE_ERROR(415, "UNSUPPORTED_MEDIA_TYPE", "지원하지 않는 Media type입니다."),
    SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 에러, 관리자에게 문의해주세요."),
    UNAUTHORIZED_ERROR(401, "UNAUTHORIZED", "인증되지 않았습니다."),
    ACCESS_DENIED(403, "FORBIDDEN", "올바르지 않은 권한입니다."),
    NOT_FOUND_DATA(404, "NOT_FOUND", "해당 데이터가 존재하지 않습니다."),
    UPLOAD_FAILED(500, "INTERNAL_SERVER_ERROR", "파일 업로드에 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;

    GlobalErrorCode(int status, String code, String message) {
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
