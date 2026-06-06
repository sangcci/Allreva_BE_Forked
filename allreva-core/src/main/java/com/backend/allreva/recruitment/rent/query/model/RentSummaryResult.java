package com.backend.allreva.recruitment.rent.query.model;

import com.backend.allreva.recruitment.rent.domain.Rent;
import com.backend.allreva.recruitment.rent.domain.Route;
import java.time.LocalDate;

public record RentSummaryResult(
        Long rentId, String title, String region, Route upRoute, Route downRoute, LocalDate endDate, String imageUrl) {
    public static RentSummaryResult from(final Rent rent) {
        return new RentSummaryResult(
                rent.getId(),
                rent.getTitle(),
                rent.getRegion(),
                rent.getUpRoute(),
                rent.getDownRoute(),
                rent.getEndDate(),
                rent.getImage().getUrl());
    }

    public static RentSummaryResult from(final RentSummary rent) {
        return new RentSummaryResult(
                rent.rentId(),
                rent.title(),
                rent.region(),
                rent.upRoute(),
                rent.downRoute(),
                rent.endDate(),
                rent.imageUrl());
    }
}
