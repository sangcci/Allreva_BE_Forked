package com.backend.allreva.module.review.concert_review.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.model.Image;
import com.backend.allreva.common.storage.upload.StorageUploadService;
import com.backend.allreva.module.review.concert_review.domain.SeatReviewImage;
import com.backend.allreva.module.review.concert_review.domain.SeatReviewImageRepository;
import com.backend.allreva.module.review.concert_review.exception.ReviewErrorCode;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewImageService {

    private final SeatReviewImageRepository seatReviewImageRepository;
    private final StorageUploadService storageUploadService;

    public void saveImageMetadata(Long seatReviewId, List<Image> uploadedImageUrls) {
        if (uploadedImageUrls == null || uploadedImageUrls.isEmpty()) {
            return;
        }

        List<SeatReviewImage> images = IntStream.range(0, uploadedImageUrls.size())
                .mapToObj(i -> SeatReviewImage.builder()
                        .url(uploadedImageUrls.get(i).getUrl())
                        .seatReviewId(seatReviewId)
                        .orderNum(i + 1)
                        .build())
                .toList();

        seatReviewImageRepository.saveAll(images);
    }

    public void deleteImages(Long seatReviewId) {
        List<SeatReviewImage> images = seatReviewImageRepository.findBySeatReviewId(seatReviewId);

        if (images.isEmpty()) {
            return;
        }

        List<String> imageUrls = images.stream().map(SeatReviewImage::getUrl).collect(Collectors.toList());

        try {
            storageUploadService.deleteImages(imageUrls);
        } catch (Exception e) {
            throw new CustomException(ReviewErrorCode.REVIEW_IMAGE_DELETE_FAILED);
        }

        seatReviewImageRepository.deleteAll(images);
    }
}
