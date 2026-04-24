package com.backend.allreva.module.search.application.dto;

import java.time.LocalDate;

public record ConcertThumbnail(
        String poster,
        String title,
        String concertHallName,
        LocalDate stdate,
        LocalDate eddate,
        String concertCode,
        java.util.List<String> episodes,
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
