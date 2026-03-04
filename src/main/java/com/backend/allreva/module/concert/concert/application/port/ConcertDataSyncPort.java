package com.backend.allreva.module.concert.concert.application.port;

import com.backend.allreva.module.concert.concert.domain.Concert;
import java.util.List;

public interface ConcertDataSyncPort {
    List<String> fetchDailyConcertCodes(String hallCode, String startDate, String endDate, String today);

    Concert fetchConcertDetail(String hallCode, String concertCode);
}
