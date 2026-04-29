package com.backend.allreva.common.storage.presigned;

import com.backend.allreva.common.storage.presigned.dto.PresignedUrlRequest;
import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "스토리지 API", description = "S3 Presigned URL 관련 API")
public interface PresignedUrlControllerSwagger {

    @SecurityRequirement(name = "USER")
    @Operation(summary = "파일 업로드 URL 생성", description = "**[회원]** S3 직접 업로드용 Presigned PUT URL 생성. 10분간 유효")
    Response<String> generateUploadUrl(PresignedUrlRequest request, Member member);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "파일 삭제 URL 생성", description = "**[회원]** S3 직접 삭제용 Presigned DELETE URL 생성. 10분간 유효")
    Response<String> generateDeleteUrl(String fileUrl);
}
