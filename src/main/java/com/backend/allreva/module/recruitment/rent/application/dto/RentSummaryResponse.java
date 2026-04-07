package com.backend.allreva.module.recruitment.rent.application.dto;

import com.backend.allreva.module.recruitment.rent.domain.Rent;
import com.backend.allreva.module.recruitment.rent.domain.value.Route;
import java.time.LocalDate;

public record RentSummaryResponse(
        Long rentId, String title, String region, Route upRoute, Route downRoute, LocalDate endDate, String imageUrl) {
    public static RentSummaryResponse from(final Rent rent) {
        return new RentSummaryResponse(
                rent.getId(),
                rent.getTitle(),
                rent.getRegion(),
                rent.getUpRoute(),
                rent.getDownRoute(),
                rent.getEndDate(),
                rent.getImage().getUrl());
    }
}
