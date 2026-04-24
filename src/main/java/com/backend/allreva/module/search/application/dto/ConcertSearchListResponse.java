package com.backend.allreva.module.search.application.dto;

import java.util.List;

public record ConcertSearchListResponse(List<ConcertThumbnail> concertThumbnails, String nextCursorCode) {
    public static ConcertSearchListResponse from(
            final List<ConcertThumbnail> concertThumbnails, final String nextCursorCode) {
        return new ConcertSearchListResponse(concertThumbnails, nextCursorCode);
    }
}
