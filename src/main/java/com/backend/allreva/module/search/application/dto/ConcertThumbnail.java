package com.backend.allreva.module.search.application.dto;

import com.backend.allreva.module.search.domain.ConcertDocument;

import java.time.LocalDate;
import java.util.List;

public record ConcertThumbnail(
        String poster,
        String title,
        String concertHallName,
        LocalDate stdate,
        LocalDate eddate,
        Long id,
        List<String> episodes,
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

        /**
         * Elasticsearch Document에서 변환
         */
        public static ConcertThumbnail from(final ConcertDocument concertDocument) {
            return new ConcertThumbnail(
                    concertDocument.getPoster(),
                    concertDocument.getTitle(),
                    concertDocument.getConcertHallName(),
                    concertDocument.getStDate(),
                    concertDocument.getEdDate(),
                    Long.parseLong(concertDocument.getId()),
                    concertDocument.getEpisodes(),
                    concertDocument.getHallCode()
            );
        }
}
