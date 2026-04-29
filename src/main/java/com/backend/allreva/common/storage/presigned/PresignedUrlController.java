package com.backend.allreva.common.storage.presigned;

import com.backend.allreva.common.storage.presigned.dto.PresignedUrlRequest;
import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.auth.security.AuthMember;
import com.backend.allreva.module.member.domain.Member;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/storage/presigned-urls")
@RequiredArgsConstructor
public class PresignedUrlController implements PresignedUrlControllerSwagger {

    private final PresignedUrlService presignedUrlService;

    @Override
    @PostMapping
    public Response<String> generateUploadUrl(
            @Valid @RequestBody PresignedUrlRequest request, @Parameter(hidden = true) @AuthMember Member member) {
        log.info("Generating upload presigned URL for file: {} (type: {})", request.fileName(), request.fileType());
        return Response.onSuccess(presignedUrlService.generateUploadUrl(request, member));
    }

    @Override
    @PostMapping("/delete")
    public Response<String> generateDeleteUrl(
            @Parameter(description = "삭제할 파일의 전체 S3 URL") @RequestParam String fileUrl) {
        log.info("Generating delete presigned URL for file: {}", fileUrl);
        return Response.onSuccess(presignedUrlService.generateDeleteUrl(fileUrl));
    }
}
