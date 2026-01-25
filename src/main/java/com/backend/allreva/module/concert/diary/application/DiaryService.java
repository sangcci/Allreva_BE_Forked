package com.backend.allreva.module.concert.diary.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.model.Image;
import com.backend.allreva.common.storage.upload.StorageUploadService;
import com.backend.allreva.module.concert.diary.application.dto.AddDiaryRequest;
import com.backend.allreva.module.concert.diary.application.dto.DiaryDetailResponse;
import com.backend.allreva.module.concert.diary.application.dto.DiarySummaryResponse;
import com.backend.allreva.module.concert.diary.application.dto.UpdateDiaryRequest;
import com.backend.allreva.module.concert.diary.domain.ConcertDiary;
import com.backend.allreva.module.concert.diary.domain.DiaryRepository;
import com.backend.allreva.module.concert.diary.exception.DiaryErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final StorageUploadService storageUploadService;

    @Transactional
    public Long add(final AddDiaryRequest request, final Long memberId) {
        ConcertDiary diary = ConcertDiary.builder()
                .concertId(request.concertId())
                .diaryDate(request.date())
                .episode(request.episode())
                .content(request.content())
                .seatName(request.seatName())
                .build();

        diary.addImages(request.images());
        diary.addMemberId(memberId);

        return diaryRepository.save(diary).getId();
    }

    @Transactional
    public void update(final UpdateDiaryRequest request, final Long memberId) {
        ConcertDiary diary = diaryRepository.findById(request.diaryId())
                .orElseThrow(() -> new CustomException(DiaryErrorCode.DIARY_NOT_FOUND));

        diary.validateWriter(memberId);
        diary.update(
                request.concertId(),
                request.date(),
                request.episode(),
                request.content(),
                request.seatName(),
                request.images());
    }

    @Transactional
    public void delete(final Long diaryId, final Long memberId) {
        ConcertDiary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new CustomException(DiaryErrorCode.DIARY_NOT_FOUND));
        diary.validateWriter(memberId);

        List<String> diaryImages = diary.getDiaryImages().stream()
                .map(Image::getUrl)
                .toList();
        storageUploadService.deleteImages(diaryImages);

        diaryRepository.deleteById(diaryId);
    }

    @Transactional
    public void addImagesById(final Long diaryId, final List<Image> images) {
        ConcertDiary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new CustomException(DiaryErrorCode.DIARY_NOT_FOUND));
        diary.addImages(images);
    }

    @Transactional(readOnly = true)
    public DiaryDetailResponse findDetailById(final Long diaryId, final Long memberId) {
        return diaryRepository.findDetail(diaryId, memberId);
    }

    @Transactional(readOnly = true)
    public List<DiarySummaryResponse> findSummaries(final Long memberId, final int year, final int month) {
        return diaryRepository.findSummaries(memberId, year, month);
    }
}
