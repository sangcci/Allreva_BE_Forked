package com.backend.allreva.module.concert.place.application.dto;

import java.time.LocalDate;

public record RelatedConcertResponse(
        Long id,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        String imageUrl,
        Long viewCount
) {
}
