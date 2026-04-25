package com.backend.allreva.module.concert.concert.infra.kopis;

import com.backend.allreva.module.concert.concert.application.dto.ConcertSummary;
import com.backend.allreva.module.concert.concert.application.port.ConcertDataSyncPort;
import com.backend.allreva.module.concert.concert.domain.Concert;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Kopis Concert API Adapter
 *
 * <p>Kopis API를 호출하여 공연 정보를 조회하고 도메인 객체로 변환합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KopisConcertDataSync implements ConcertDataSyncPort {
    private static final String POPULAR_MUSIC_GENRE_CODE = "CCCD";

    private final KopisConcertClient kopisConcertClient;

    @Override
    public List<ConcertSummary> fetchDailyConcertSummaries(
            final String hallCode, final String startDate, final String endDate, final String today) {
        return kopisConcertClient
                .fetchConcertCodes(hallCode, startDate, endDate, today, POPULAR_MUSIC_GENRE_CODE)
                .getDbList()
                .stream()
                .map(ConcertSummary::from)
                .toList();
    }

    @Override
    public Concert fetchConcertDetail(final String hallCode, final String concertCode) {
        KopisConcertResponse response = kopisConcertClient.fetchConcertDetail(concertCode);
        return KopisConcertResponse.toEntity(hallCode, response);
    }
}
