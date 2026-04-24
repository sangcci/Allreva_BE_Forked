package com.backend.allreva.module.concert.concert.application.dto;

import com.backend.allreva.module.concert.concert.domain.value.ConcertStatus;
import com.backend.allreva.module.concert.concert.infra.kopis.KopisConcertCodeResponse;

public record ConcertSummary(String concertCode, ConcertStatus status) {
    public static ConcertSummary from(final KopisConcertCodeResponse.Db db) {
        return new ConcertSummary(db.getId(), ConcertStatus.convertToConcertStatus(db.getPrfState()));
    }
}
