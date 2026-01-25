package com.backend.allreva.module.concert.diary.application.dto;

import com.backend.allreva.common.model.Image;

import java.time.LocalDate;

public record DiarySummaryResponse(
        Long diaryId,
        Image concertPoster,
        LocalDate date) {
}
