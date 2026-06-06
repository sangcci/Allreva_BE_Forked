package com.backend.allreva.concert.concert.command.implementation;

import com.backend.allreva.concert.concert.domain.Concert;
import com.backend.allreva.concert.concert.domain.ConcertSyncPeriod;
import com.backend.allreva.concert.concert.query.model.ConcertSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcertRefresher {

    private final ConcertDataSyncPort concertDataSyncPort;
    private final ConcertWriter concertWriter;

    public void refresh(final String hallCode, final ConcertSyncPeriod period) {
        for (ConcertSummary summary : concertDataSyncPort.fetchDailyConcertSummaries(hallCode, period)) {
            Concert fetched = concertDataSyncPort.fetchConcertDetail(hallCode, summary.concertCode());
            concertWriter.upsert(fetched);
        }
    }
}
