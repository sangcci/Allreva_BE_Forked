package com.backend.allreva.module.concert.diary.application.dto;

import com.backend.allreva.common.model.Image;

import java.time.LocalDate;
import java.util.List;

public record DiaryDetailResponse(
        String concertTitle,
        Image concertPoster,
        LocalDate diaryDate,
        String episode,
        String seatName,
        List<Image> diaryImages,
        String content) {
}
