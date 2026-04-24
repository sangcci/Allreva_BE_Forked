package com.backend.allreva.module.search.application.dto;

import java.util.List;

public record ConcertMainResponse(List<ConcertThumbnail> concertThumbnails, String nextCursorCode) {
    public static ConcertMainResponse from(
            final List<ConcertThumbnail> concertThumbnails, final String nextCursorCode) {
        return new ConcertMainResponse(concertThumbnails, nextCursorCode);
    }
}
