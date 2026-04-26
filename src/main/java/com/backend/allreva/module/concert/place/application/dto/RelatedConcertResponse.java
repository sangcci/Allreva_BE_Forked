package com.backend.allreva.module.concert.place.application.dto;

import com.backend.allreva.module.concert.concert.domain.Concert;
import java.time.LocalDate;

public record RelatedConcertResponse(
        String concertCode, String title, LocalDate startDate, LocalDate endDate, String posterUrl) {

    public static RelatedConcertResponse from(final Concert concert) {
        return new RelatedConcertResponse(
                concert.getConcertCode(),
                concert.getConcertInfo().getTitle(),
                concert.getConcertInfo().getDateInfo().getStartDate(),
                concert.getConcertInfo().getDateInfo().getEndDate(),
                concert.getPoster().getUrl());
    }
}
