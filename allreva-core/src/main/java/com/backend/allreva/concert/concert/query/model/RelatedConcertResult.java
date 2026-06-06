package com.backend.allreva.concert.concert.query.model;

import com.backend.allreva.concert.concert.domain.Concert;
import java.time.LocalDate;

public record RelatedConcertResult(
        String concertCode, String title, LocalDate startDate, LocalDate endDate, String posterUrl) {

    public static RelatedConcertResult from(final Concert concert) {
        return new RelatedConcertResult(
                concert.getConcertCode(),
                concert.getConcertInfo().getTitle(),
                concert.getConcertInfo().getDateInfo().getStartDate(),
                concert.getConcertInfo().getDateInfo().getEndDate(),
                concert.getPoster() != null ? concert.getPoster().getUrl() : null);
    }

    public static RelatedConcertResult from(final RelatedConcert concert) {
        return new RelatedConcertResult(
                concert.concertCode(), concert.title(), concert.startDate(), concert.endDate(), concert.posterUrl());
    }
}
