package com.backend.allreva.diary.command.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.common.storage.upload.StorageUploadService;
import com.backend.allreva.diary.command.application.request.AddDiaryRequest;
import com.backend.allreva.diary.command.application.request.UpdateDiaryRequest;
import com.backend.allreva.diary.command.domain.ConcertDiary;
import com.backend.allreva.diary.command.domain.DiaryRepository;
import com.backend.allreva.diary.exception.DiaryNotFoundException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class DiaryCommandService {

    private final DiaryRepository diaryRepository;
    private final StorageUploadService storageUploadService;

    public Long add(
            final AddDiaryRequest request,
            final Long memberId) {
        ConcertDiary diary = request.to();

        diary.addImages(request.images());
        diary.addMemberId(memberId);
        return diaryRepository.save(diary).getId();
    }

    public void update(
            final UpdateDiaryRequest request,
            final Long memberId) {
        ConcertDiary diary = diaryRepository.findById(request.diaryId())
                .orElseThrow(DiaryNotFoundException::new);

        diary.validateWriter(memberId);
        diary.update(
                request.concertId(),
                request.date(),
                request.episode(),
                request.content(),
                request.seatName(),
                request.images());
    }

    public void delete(final Long diaryId, final Long memberId) {
        ConcertDiary diary = diaryRepository.findById(diaryId)
                .orElseThrow(DiaryNotFoundException::new);
        diary.validateWriter(memberId);

        List<String> diaryImages = diary.getDiaryImages().stream().map(Image::getUrl).toList();
        storageUploadService.deleteImages(diaryImages);

        diaryRepository.deleteById(diaryId);
    }

    public void addImagesById(Long diaryId, List<Image> images) {
        ConcertDiary diary = diaryRepository.findById(diaryId)
                .orElseThrow();
        diary.addImages(images);
    }
}
