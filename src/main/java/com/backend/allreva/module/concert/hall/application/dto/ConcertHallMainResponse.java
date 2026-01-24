package com.backend.allreva.module.concert.hall.application.dto;

import java.util.List;

public record ConcertHallMainResponse(
        List<ConcertHallThumbnail> concertHallThumbnails,
        List<Object> searchAfter
) {
    public static ConcertHallMainResponse from(final List<ConcertHallThumbnail> thumbnails, final List<Object> searchAfter) {
        return new ConcertHallMainResponse(thumbnails, searchAfter);
    }
}
