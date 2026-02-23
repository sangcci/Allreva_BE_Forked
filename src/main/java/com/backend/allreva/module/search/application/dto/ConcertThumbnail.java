package com.backend.allreva.module.search.application.dto;

import java.time.LocalDate;

public record ConcertThumbnail(
        String poster,
        String title,
        String concertHallName,
        LocalDate stdate,
        LocalDate eddate,
        Long id,
        java.util.List<String> episodes,
        String hallId
) {
    /**
     * QueryDSL용 생성자 (RDB 조회 시 사용)
     */
    public ConcertThumbnail(
            String poster,
            String title,
            String concertHallName,
            LocalDate stdate,
            LocalDate eddate,
            Long id
    ) {
        this(poster, title, concertHallName, stdate, eddate, id, null, null);
    }
}
