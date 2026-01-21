package com.backend.allreva.common.storage.presigned;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.allreva.common.storage.presigned.dto.PresignedUrlRequest;
import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.member.command.domain.Member;
import com.backend.allreva.module.auth.security.AuthMember;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/storage/presigned-urls")
@RequiredArgsConstructor
@Tag(name = "Storage - Presigned URL", description = "S3 Presigned URL 생성 API")
public class PresignedUrlController {

    private final PresignedUrlService presignedUrlService;

    /**
     * 파일 업로드용 Presigned URL 생성
     *
     * @param request 파일명과 파일 타입 정보
     * @param member  인증된 회원 정보 (선택적)
     * @return 10분간 유효한 Presigned PUT URL
     */
    @PostMapping
    @Operation(summary = "파일 업로드 URL 생성", description = "클라이언트가 S3에 직접 파일을 업로드할 수 있는 임시 URL을 생성합니다. " +
            "생성된 URL은 10분간 유효하며, PUT 메서드로 파일을 업로드할 수 있습니다.\n\n" +
            "파일 타입별 저장 경로:\n" +
            "- PROFILE: [닉네임]/PROFILE/UUID_파일명\n" +
            "- CHAT: [닉네임]/CHAT/UUID_파일명\n" +
            "- REVIEW: [닉네임]/REVIEW/UUID_파일명\n" +
            "- SURVEY: [닉네임]/SURVEY/UUID_파일명\n" +
            "- RENT: [닉네임]/RENT/UUID_파일명")
    public Response<String> generateUploadUrl(
            @Valid @RequestBody PresignedUrlRequest request,
            @Parameter(hidden = true) @AuthMember Member member) {

        log.info("Generating upload presigned URL for file: {} (type: {})",
                request.fileName(), request.fileType());

        String presignedUrl = presignedUrlService.generateUploadUrl(request, member);

        return Response.onSuccess(presignedUrl);
    }

    /**
     * 파일 삭제용 Presigned URL 생성
     *
     * @param fileUrl 삭제할 파일의 전체 S3 URL
     * @return 10분간 유효한 Presigned DELETE URL
     */
    @PostMapping("/delete")
    @Operation(summary = "파일 삭제 URL 생성", description = "클라이언트가 S3에서 직접 파일을 삭제할 수 있는 임시 URL을 생성합니다. " +
            "생성된 URL은 10분간 유효하며, DELETE 메서드로 파일을 삭제할 수 있습니다.")
    public Response<String> generateDeleteUrl(
            @Parameter(description = "삭제할 파일의 전체 S3 URL", example = "https://bucket.s3.amazonaws.com/path/file.jpg") @RequestParam String fileUrl) {

        log.info("Generating delete presigned URL for file: {}", fileUrl);

        String presignedUrl = presignedUrlService.generateDeleteUrl(fileUrl);

        return Response.onSuccess(presignedUrl);
    }
}
