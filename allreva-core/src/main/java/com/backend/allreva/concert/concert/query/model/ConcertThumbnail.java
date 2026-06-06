package com.backend.allreva.concert.concert.query.model;

import java.time.LocalDate;
import java.util.List;

public record ConcertThumbnail(
        String poster,
        String title,
        String concertHallName,
        LocalDate stdate,
        LocalDate eddate,
        String concertCode,
        List<String> episodes,
        String hallId) {
    public ConcertThumbnail(
            String poster,
            String title,
            String concertHallName,
            LocalDate stdate,
            LocalDate eddate,
            String concertCode) {
        this(poster, title, concertHallName, stdate, eddate, concertCode, null, null);
    }
}
