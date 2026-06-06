package com.backend.allreva.recruitment.rent.query.model;

import java.time.LocalDate;

public record RentThumbnailResult(Long rentId, String title, String region, String imageUrl, LocalDate endDate) {

    public static RentThumbnailResult from(final RentThumbnail rent) {
        return new RentThumbnailResult(rent.id(), rent.title(), rent.region(), rent.imageUrl(), rent.endDate());
    }
}
