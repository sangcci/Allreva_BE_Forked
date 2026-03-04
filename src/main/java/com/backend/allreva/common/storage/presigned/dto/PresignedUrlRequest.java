package com.backend.allreva.common.storage.presigned.dto;

import com.backend.allreva.common.storage.presigned.FileType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Presigned URL 생성 요청 DTO
 *
 * <p>클라이언트가 S3에 직접 파일을 업로드하기 위한 임시 서명된 URL을 요청할 때 사용합니다.
 */
@Schema(description = "Presigned URL 생성 요청")
public record PresignedUrlRequest(
        @NotBlank(message = "파일 이름은 필수입니다.") @Schema(description = "업로드할 파일 이름", example = "profile.jpg")
        String fileName,

        @NotNull(message = "파일 타입은 필수입니다.")
        @Schema(description = "파일 타입 (PROFILE, CHAT, REVIEW, SURVEY, RENT)", example = "PROFILE")
        FileType fileType) {}
