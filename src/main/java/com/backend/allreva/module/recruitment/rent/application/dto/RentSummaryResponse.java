package com.backend.allreva.module.recruitment.rent.application.dto;

import com.backend.allreva.module.recruitment.rent.domain.Rent;
import java.time.LocalDate;

public record RentSummaryResponse(Long rentId, String title, String boardingArea, LocalDate endDate, String imageUrl) {
    public static RentSummaryResponse from(final Rent rent) {
        return new RentSummaryResponse(
                rent.getId(),
                rent.getTitle(),
                rent.getBoardingArea(),
                rent.getEndDate(),
                rent.getImage().getUrl());
    }
}
