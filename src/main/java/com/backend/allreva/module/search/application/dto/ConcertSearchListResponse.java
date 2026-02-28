package com.backend.allreva.module.search.application.dto;

import java.util.List;

public record ConcertSearchListResponse(List<ConcertThumbnail> concertThumbnails, Long nextCursorId) {
    public static ConcertSearchListResponse from(
            final List<ConcertThumbnail> concertThumbnails, final Long nextCursorId) {
        return new ConcertSearchListResponse(concertThumbnails, nextCursorId);
    }
}
