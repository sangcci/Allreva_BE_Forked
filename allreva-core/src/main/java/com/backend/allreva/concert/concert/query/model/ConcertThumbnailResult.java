package com.backend.allreva.concert.concert.query.model;

import java.time.LocalDate;
import java.util.List;

public record ConcertThumbnailResult(
        String poster,
        String title,
        String concertHallName,
        LocalDate stdate,
        LocalDate eddate,
        String concertCode,
        List<String> episodes,
        String hallId) {

    public static ConcertThumbnailResult from(final ConcertThumbnail concert) {
        return new ConcertThumbnailResult(
                concert.poster(),
                concert.title(),
                concert.concertHallName(),
                concert.stdate(),
                concert.eddate(),
                concert.concertCode(),
                concert.episodes(),
                concert.hallId());
    }
}
