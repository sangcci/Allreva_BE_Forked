package com.backend.allreva.module.search.application.dto;

import java.util.List;

public record ConcertMainResponse(List<ConcertThumbnail> concertThumbnails, Long nextCursorId) {
    public static ConcertMainResponse from(final List<ConcertThumbnail> concertThumbnails, final Long nextCursorId) {
        return new ConcertMainResponse(concertThumbnails, nextCursorId);
    }
}
