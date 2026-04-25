package com.backend.allreva.module.concert.concert.application.port;

import com.backend.allreva.module.concert.concert.application.dto.ConcertSummary;
import com.backend.allreva.module.concert.concert.domain.Concert;
import java.time.LocalDate;
import java.util.List;

public interface ConcertDataSyncPort {
    List<ConcertSummary> fetchDailyConcertSummaries(
            String hallCode, LocalDate startDate, LocalDate endDate, LocalDate today);

    Concert fetchConcertDetail(String hallCode, String concertCode);
}
