package com.backend.allreva.common.storage.presigned;

import com.backend.allreva.common.storage.exception.InvalidUrlException;
import com.backend.allreva.common.storage.exception.UploadFailedException;
import com.backend.allreva.common.storage.presigned.dto.PresignedUrlRequest;
import com.backend.allreva.member.command.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.DeleteObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedDeleteObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PresignedUrlService {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    private final S3Presigner s3Presigner;

    private static final int URL_EXPIRATION_MINUTES = 10;

    /**
     * 파일 업로드를 위한 Presigned URL 생성
     *
     * @param request 파일명과 파일 타입 정보
     * @param member  업로드하는 회원 정보 (null 가능)
     * @return Presigned PUT URL
     */
    public String generateUploadUrl(final PresignedUrlRequest request, final Member member) {
        String objectKey = buildObjectKey(request, member);

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(URL_EXPIRATION_MINUTES))
                .putObjectRequest(objectRequest)
                .build();

        try {
            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
            String url = presignedRequest.url().toString();
            log.debug("Generated presigned upload URL for key: {}", objectKey);
            return url;
        } catch (Exception e) {
            log.error("Failed to generate presigned upload URL for key: {}", objectKey, e);
            throw new UploadFailedException();
        }
    }

    /**
     * 파일 삭제를 위한 Presigned URL 생성
     *
     * @param fileUrl 삭제할 파일의 전체 URL
     * @return Presigned DELETE URL
     */
    public String generateDeleteUrl(final String fileUrl) {
        String objectKey = extractObjectKeyFromUrl(fileUrl);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();

        DeleteObjectPresignRequest presignDeleteRequest = DeleteObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(URL_EXPIRATION_MINUTES))
                .deleteObjectRequest(deleteRequest)
                .build();

        try {
            PresignedDeleteObjectRequest presignedDeleteRequest = s3Presigner.presignDeleteObject(presignDeleteRequest);
            return presignedDeleteRequest.url().toString();
        } catch (Exception e) {
            log.error("Failed to generate presigned delete URL for key: {}", objectKey, e);
            throw new InvalidUrlException();
        }
    }

    /**
     * S3 객체 키 생성
     *
     * 경로 구조: [닉네임/]파일타입/UUID_파일명
     * 예: john/PROFILE/a1b2c3d4_profile.jpg
     */
    private String buildObjectKey(final PresignedUrlRequest request, final Member member) {
        StringBuilder keyBuilder = new StringBuilder();

        // 회원이 있으면 닉네임 폴더 추가
        if (member != null) {
            keyBuilder.append(member.getMemberInfo().getNickname())
                    .append("/");
        }

        // 파일 타입 폴더 추가
        keyBuilder.append(request.fileType().toString())
                .append("/");

        // UUID와 파일명 추가 (하이픈 제거)
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        keyBuilder.append(uuid)
                .append("_")
                .append(request.fileName());

        return keyBuilder.toString();
    }

    /**
     * S3 URL에서 객체 키 추출
     *
     * @param fileUrl 전체 S3 URL
     * @return 객체 키 (버킷명 이후의 경로)
     */
    private String extractObjectKeyFromUrl(String fileUrl) {
        String bucketUrl = "https://" + bucket + ".s3.us-east-2.amazonaws.com/";

        if (fileUrl.startsWith(bucketUrl)) {
            return fileUrl.substring(bucketUrl.length());
        } else {
            throw new IllegalArgumentException("Invalid S3 file URL: " + fileUrl);
        }
    }
}
