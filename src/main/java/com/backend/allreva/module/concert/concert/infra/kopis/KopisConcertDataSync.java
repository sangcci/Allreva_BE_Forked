package com.backend.allreva.module.concert.concert.infra.kopis;

import com.backend.allreva.module.concert.concert.application.port.ConcertDataSyncPort;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.infra.kopis.KopisConcertCodeResponse.Db;
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
    private final KopisConcertClient kopisConcertClient;

    @Override
    public List<String> fetchDailyConcertCodes(String hallCode, String startDate, String endDate, String today) {
        return kopisConcertClient.fetchConcertCodes(hallCode, startDate, endDate, today).getDbList().stream()
                .map(Db::getId)
                .toList();
    }

    @Override
    public Concert fetchConcertDetail(String hallCode, String concertCode) {
        KopisConcertResponse response = kopisConcertClient.fetchConcertDetail(concertCode);
        return KopisConcertResponse.toEntity(hallCode, response);
    }
}
