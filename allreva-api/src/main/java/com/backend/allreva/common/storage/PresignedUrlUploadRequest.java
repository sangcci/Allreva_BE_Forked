package com.backend.allreva.common.storage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PresignedUrlUploadRequest(
        @NotBlank(message = "파일 이름은 필수입니다.") String fileName,
        @NotNull(message = "파일 목적은 필수입니다.") StoragePurpose purpose) {

    public PresignedUrlRequest toCommand() {
        return new PresignedUrlRequest(fileName, purpose);
    }
}
