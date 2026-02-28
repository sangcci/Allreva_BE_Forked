package com.backend.allreva.module.concert.place.application.dto;

import java.util.List;

public record ConcertHallMainResponse(List<ConcertHallThumbnail> concertHallThumbnails, String nextCursorId) {
    public static ConcertHallMainResponse from(final List<ConcertHallThumbnail> thumbnails, final String nextCursorId) {
        return new ConcertHallMainResponse(thumbnails, nextCursorId);
    }
}
