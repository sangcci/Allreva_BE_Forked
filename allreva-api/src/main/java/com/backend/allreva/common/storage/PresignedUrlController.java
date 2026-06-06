package com.backend.allreva.common.storage;

import com.backend.allreva.auth.security.AuthMember;
import com.backend.allreva.common.web.response.View;
import com.backend.allreva.member.domain.Member;
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
    public View<String> generateUploadUrl(
            @Valid @RequestBody PresignedUrlUploadRequest request,
            @Parameter(hidden = true) @AuthMember Member member) {
        log.info("Generating upload presigned URL for file: {} (purpose: {})", request.fileName(), request.purpose());
        return View.onSuccess(presignedUrlService.generateUploadUrl(request.toCommand(), member));
    }

    @Override
    @PostMapping("/delete")
    public View<String> generateDeleteUrl(@Parameter(description = "삭제할 파일의 전체 S3 URL") @RequestParam String fileUrl) {
        log.info("Generating delete presigned URL for file: {}", fileUrl);
        return View.onSuccess(presignedUrlService.generateDeleteUrl(fileUrl));
    }
}
