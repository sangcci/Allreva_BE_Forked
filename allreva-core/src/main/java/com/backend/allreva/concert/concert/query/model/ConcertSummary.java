package com.backend.allreva.concert.concert.query.model;

import com.backend.allreva.concert.concert.domain.ConcertStatus;

public record ConcertSummary(String concertCode, ConcertStatus status) {
    public static ConcertSummary from(final String concertCode, final String status) {
        return new ConcertSummary(concertCode, ConcertStatus.convertToConcertStatus(status));
    }
}
