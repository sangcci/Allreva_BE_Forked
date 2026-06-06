package com.backend.allreva.common.storage;

import com.backend.allreva.common.config.StorageProperties;
import com.backend.allreva.common.exception.CustomException;
import java.net.URI;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.DeleteObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedDeleteObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3StorageAdapter implements StoragePort {

    private static final int URL_EXPIRATION_MINUTES = 10;

    private final StorageProperties storageProperties;
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Override
    public void delete(final String fileUrl) {
        String objectKey = extractObjectKeyFromUrl(fileUrl);

        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket())
                    .key(objectKey)
                    .build());
        } catch (Exception e) {
            log.error("Failed to delete file from S3 - key: {}", objectKey, e);
            throw new CustomException(StorageErrorCode.FILE_DELETE_FAILED, e);
        }
    }

    @Override
    public String generateUploadPresignedUrl(final String objectKey) {
        PutObjectRequest objectRequest =
                PutObjectRequest.builder().bucket(bucket()).key(objectKey).build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(URL_EXPIRATION_MINUTES))
                .putObjectRequest(objectRequest)
                .build();

        try {
            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
            return presignedRequest.url().toString();
        } catch (Exception e) {
            log.error("Failed to generate S3 presigned upload URL - key: {}", objectKey, e);
            throw new CustomException(StorageErrorCode.PRESIGNED_URL_GENERATION_FAILED, e);
        }
    }

    @Override
    public String generateDeletePresignedUrl(final String fileUrl) {
        String objectKey = extractObjectKeyFromUrl(fileUrl);

        DeleteObjectRequest deleteRequest =
                DeleteObjectRequest.builder().bucket(bucket()).key(objectKey).build();

        DeleteObjectPresignRequest presignDeleteRequest = DeleteObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(URL_EXPIRATION_MINUTES))
                .deleteObjectRequest(deleteRequest)
                .build();

        try {
            PresignedDeleteObjectRequest presignedDeleteRequest = s3Presigner.presignDeleteObject(presignDeleteRequest);
            return presignedDeleteRequest.url().toString();
        } catch (Exception e) {
            log.error("Failed to generate S3 presigned delete URL - key: {}", objectKey, e);
            throw new CustomException(StorageErrorCode.PRESIGNED_URL_GENERATION_FAILED, e);
        }
    }

    private String extractObjectKeyFromUrl(final String fileUrl) {
        URI uri = URI.create(fileUrl);
        String host = uri.getHost();
        String path = uri.getPath();

        if (host != null && host.equals(bucket() + ".s3." + region() + ".amazonaws.com")) {
            return removeLeadingSlash(path);
        }

        if (host != null && host.equals("s3." + region() + ".amazonaws.com")) {
            String bucketPrefix = "/" + bucket() + "/";
            if (path.startsWith(bucketPrefix)) {
                return path.substring(bucketPrefix.length());
            }
        }

        log.warn("Invalid S3 URL format - bucket: {}, region: {}, actual: {}", bucket(), region(), fileUrl);
        throw new CustomException(StorageErrorCode.INVALID_S3_URL);
    }

    private String bucket() {
        return storageProperties.s3().bucket();
    }

    private String region() {
        return storageProperties.region().staticRegion();
    }

    private String removeLeadingSlash(final String path) {
        if (path.startsWith("/")) {
            return path.substring(1);
        }
        return path;
    }
}
