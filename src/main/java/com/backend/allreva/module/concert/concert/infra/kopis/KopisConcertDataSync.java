package com.backend.allreva.module.concert.concert.infra.kopis;

import com.backend.allreva.module.concert.concert.application.dto.ConcertSummary;
import com.backend.allreva.module.concert.concert.application.port.ConcertDataSyncPort;
import com.backend.allreva.module.concert.concert.domain.Concert;
import java.time.LocalDate;
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

    @Override
    public List<ConcertSummary> fetchDailyConcertSummaries(
            final String hallCode, final LocalDate startDate, final LocalDate endDate, final LocalDate today) {
        return kopisConcertClient
                .fetchConcertSummaries(
                        hallCode,
                        KopisDateConverter.toKopisFormat(startDate),
                        KopisDateConverter.toKopisFormat(endDate),
                        KopisDateConverter.toKopisFormat(today),
                        POPULAR_MUSIC_GENRE_CODE)
                .getDbList()
                .stream()
                .map(ConcertSummary::from)
                .toList();
    }

    @Override
    public Concert fetchConcertDetail(final String hallCode, final String concertCode) {
        KopisConcertDetailResponse response = kopisConcertClient.fetchConcertDetail(concertCode);
        return KopisConcertDetailResponse.toEntity(hallCode, response);
    }
}
