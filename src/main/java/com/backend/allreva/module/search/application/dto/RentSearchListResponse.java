package com.backend.allreva.module.search.application.dto;

import java.util.List;

public record RentSearchListResponse(
        List<RentThumbnail> rentThumbnails,
        Long nextCursorId
) {
    public static RentSearchListResponse from(
            final List<RentThumbnail> rentThumbnails,
            final Long nextCursorId) {
        return new RentSearchListResponse(rentThumbnails, nextCursorId);
    }
}
