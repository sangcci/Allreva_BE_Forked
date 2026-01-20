package com.backend.allreva.seat_review.command.application;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.common.storage.upload.StorageUploadService;
import com.backend.allreva.seat_review.command.domain.SeatReviewImage;
import com.backend.allreva.seat_review.exception.SeatReviewImageDeleteException;
import com.backend.allreva.seat_review.infra.SeatReviewImageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatReviewImageService {

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

        List<String> imageUrls = images.stream()
                .map(SeatReviewImage::getUrl)
                .collect(Collectors.toList());

        try {
            storageUploadService.deleteImages(imageUrls);
        } catch (Exception e) {
            throw new SeatReviewImageDeleteException();
        }

        seatReviewImageRepository.deleteAll(images);
    }
}
