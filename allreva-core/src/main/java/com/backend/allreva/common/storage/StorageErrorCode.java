package com.backend.allreva.common.storage;

import com.backend.allreva.common.exception.ErrorCode;

public enum StorageErrorCode implements ErrorCode {

    // Presigned URL 관련
    PRESIGNED_URL_GENERATION_FAILED(500, "PRESIGNED_URL_GENERATION_FAILED", "Presigned URL 생성에 실패했습니다."),
    INVALID_S3_URL(400, "INVALID_S3_URL", "올바르지 않은 S3 URL 형식입니다."),

    // 파일 업로드 관련
    FILE_UPLOAD_FAILED(500, "FILE_UPLOAD_FAILED", "파일 업로드에 실패했습니다."),
    EMPTY_FILE(400, "EMPTY_FILE", "빈 파일은 업로드할 수 없습니다."),
    INVALID_FILE_TYPE(400, "INVALID_FILE_TYPE", "지원하지 않는 파일 형식입니다."),
    FILE_SIZE_EXCEEDED(400, "FILE_SIZE_EXCEEDED", "파일 크기가 제한을 초과했습니다."),

    // 파일 삭제 관련
    FILE_DELETE_FAILED(500, "FILE_DELETE_FAILED", "파일 삭제에 실패했습니다."),
    FILE_NOT_FOUND(404, "FILE_NOT_FOUND", "파일을 찾을 수 없습니다."),

    // S3 연결 관련
    S3_CONNECTION_FAILED(500, "S3_CONNECTION_FAILED", "S3 서버 연결에 실패했습니다."),
    S3_PERMISSION_DENIED(403, "S3_PERMISSION_DENIED", "S3 접근 권한이 없습니다.");

    private final int status;
    private final String code;
    private final String message;

    StorageErrorCode(int status, String code, String message) {
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
