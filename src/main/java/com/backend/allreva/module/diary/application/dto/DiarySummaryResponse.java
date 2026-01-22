package com.backend.allreva.module.diary.application.dto;

import com.backend.allreva.common.model.Image;

import java.time.LocalDate;

public record DiarySummaryResponse(
        Long diaryId,
        Image concertPoster,
        LocalDate date) {
}
