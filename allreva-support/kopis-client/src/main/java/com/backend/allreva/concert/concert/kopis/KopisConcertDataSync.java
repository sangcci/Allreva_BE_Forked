package com.backend.allreva.concert.concert.kopis;

import com.backend.allreva.concert.concert.command.implementation.ConcertDataSyncPort;
import com.backend.allreva.concert.concert.domain.Concert;
import com.backend.allreva.concert.concert.domain.ConcertSyncPeriod;
import com.backend.allreva.concert.concert.query.model.ConcertSummary;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KopisConcertDataSync implements ConcertDataSyncPort {
    private static final String POPULAR_MUSIC_GENRE_CODE = "CCCD";

    private final KopisConcertClient kopisConcertClient;
    private final KopisConcertMapper kopisConcertMapper;

    @Override
    public List<ConcertSummary> fetchDailyConcertSummaries(final String hallCode, final ConcertSyncPeriod period) {
        return kopisConcertClient
                .fetchConcertSummaries(
                        hallCode,
                        KopisDateConverter.toKopisFormat(period.startDate()),
                        KopisDateConverter.toKopisFormat(period.endDate()),
                        KopisDateConverter.toKopisFormat(period.today()),
                        POPULAR_MUSIC_GENRE_CODE)
                .getDbList()
                .stream()
                .map(db -> ConcertSummary.from(db.getId(), db.getPrfState()))
                .toList();
    }

    @Override
    public Concert fetchConcertDetail(final String hallCode, final String concertCode) {
        KopisConcertDetailResponse response = kopisConcertClient.fetchConcertDetail(concertCode);
        return kopisConcertMapper.toConcert(hallCode, response);
    }
}
