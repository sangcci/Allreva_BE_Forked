package com.backend.allreva.common.storage.upload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.exception.code.GlobalErrorCode;
import com.backend.allreva.common.model.Image;
import com.backend.allreva.seat_review.command.application.dto.FileData;

import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Operations;
import io.awspring.cloud.s3.S3Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageUploadService {

    @Value("${spring.cloud.aws.s3.bucket:null}")
    private String bucketName;

    private final S3Operations s3Operations;

    /**
     * 단일 파일 업로드 (MultipartFile)
     *
     * @param imageFile 업로드할 파일
     * @return 업로드된 파일의 S3 URL을 담은 Image 객체
     */
    public Image uploadImage(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return new Image("");
        }

        ObjectMetadata objectMetadata = ObjectMetadata.builder()
                .contentType(imageFile.getContentType())
                .build();

        String storeKey = generateStoreKey(imageFile.getOriginalFilename());

        return uploadToS3(imageFile, storeKey, objectMetadata);
    }

    /**
     * 여러 파일 일괄 업로드
     *
     * @param imageFiles 업로드할 파일 리스트
     * @return 업로드된 파일들의 URL 리스트
     */
    public List<Image> uploadImages(List<MultipartFile> imageFiles) {
        if (imageFiles == null || imageFiles.isEmpty()) {
            return Collections.emptyList();
        }

        log.info("Uploading {} images", imageFiles.size());
        List<Image> uploadedImages = new ArrayList<>();

        for (MultipartFile file : imageFiles) {
            Image uploadedImage = uploadImage(file);
            uploadedImages.add(uploadedImage);
        }

        return uploadedImages;
    }

    /**
     * 바이트 배열로 파일 업로드 (FileData)
     *
     * @param fileData 파일 데이터 (바이트 배열과 파일명)
     * @return 업로드된 파일의 S3 URL을 담은 Image 객체
     */
    public Image uploadFromBytes(FileData fileData) {
        if (fileData.bytes() == null || fileData.bytes().length == 0) {
            return new Image("");
        }

        ObjectMetadata objectMetadata = ObjectMetadata.builder()
                .contentType("application/octet-stream")
                .build();

        String storeKey = generateStoreKey(fileData.filename());

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData.bytes())) {
            S3Resource resource = s3Operations.upload(bucketName, storeKey, inputStream, objectMetadata);
            return new Image(resource.getURL().toString());
        } catch (Exception e) {
            log.error("Failed to upload file from bytes: {}", storeKey, e);
            throw new CustomException(GlobalErrorCode.SERVER_ERROR);
        }
    }

    /**
     * S3에서 이미지 삭제
     *
     * @param imageUrl 삭제할 이미지의 전체 URL
     */
    public void deleteImage(String imageUrl) {
        if (imageUrl.isEmpty()) {
            log.warn("Attempted to delete empty image URL");
            return;
        }

        try {
            String key = extractKeyFromUrl(imageUrl);
            s3Operations.deleteObject(bucketName, key);
        } catch (Exception e) {
            log.error("Failed to delete image from S3: {}", imageUrl, e);
            throw new CustomException(GlobalErrorCode.SERVER_ERROR);
        }
    }

    /**
     * 여러 이미지 일괄 삭제
     *
     * @param imageUrls 삭제할 이미지 URL 리스트
     */
    public void deleteImages(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        log.info("Deleting {} images", imageUrls.size());
        imageUrls.forEach(this::deleteImage);
    }

    /**
     * MultipartFile을 S3에 업로드
     */
    private Image uploadToS3(
            MultipartFile imageFile,
            String storeKey,
            ObjectMetadata metadata) {
        try (InputStream inputStream = imageFile.getInputStream()) {
            S3Resource resource = s3Operations.upload(bucketName, storeKey, inputStream, metadata);
            String url = resource.getURL().toString();
            return new Image(url);
        } catch (IOException e) {
            log.error("Failed to upload file to S3: {}", storeKey, e);
            throw new CustomException(GlobalErrorCode.SERVER_ERROR);
        }
    }

    /**
     * 저장용 키 생성 (UUID + 파일명)
     *
     * @param originalFilename 원본 파일명
     * @return UUID가 포함된 저장용 키
     */
    private String generateStoreKey(String originalFilename) {
        return UUID.randomUUID() + "_" + originalFilename;
    }

    /**
     * S3 URL에서 객체 키 추출
     *
     * @param imageUrl 전체 S3 URL
     * @return 객체 키 (파일명 부분)
     */
    private String extractKeyFromUrl(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }
}
