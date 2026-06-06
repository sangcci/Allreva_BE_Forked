package com.backend.allreva.concert.concert.command.implementation;

import com.backend.allreva.concert.concert.domain.Concert;
import com.backend.allreva.concert.concert.domain.ConcertSyncPeriod;
import com.backend.allreva.concert.concert.query.model.ConcertSummary;
import java.util.List;

public interface ConcertDataSyncPort {
    List<ConcertSummary> fetchDailyConcertSummaries(String hallCode, ConcertSyncPeriod period);

    Concert fetchConcertDetail(String hallCode, String concertCode);
}
