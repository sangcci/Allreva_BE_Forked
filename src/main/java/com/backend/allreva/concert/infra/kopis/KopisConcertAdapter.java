package com.backend.allreva.concert.infra.kopis;

import com.backend.allreva.concert.command.application.KopisConcertService;
import com.backend.allreva.concert.infra.dto.KopisConcertCodeResponse.Db;
import com.backend.allreva.concert.infra.dto.KopisConcertResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Kopis Concert API 어댑터
 *
 * KopisConcertClient를 사용하여 공연 정보를 조회하고 도메인에서 필요한 형태로 변환합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KopisConcertAdapter implements KopisConcertService {
    private final KopisConcertClient kopisConcertClient;

    @Override
    public List<String> fetchConcertCodes(String hallCode, String startDate, String endDate) {
        return kopisConcertClient.fetchConcertCodes(hallCode, startDate, endDate, null)
                .getDbList()
                .stream()
                .map(Db::getId)
                .toList();
    }

    @Override
    public List<String> fetchDailyConcertCodes(String hallCode, String startDate, String endDate, String today) {
        return kopisConcertClient.fetchConcertCodes(hallCode, startDate, endDate, today)
                .getDbList()
                .stream()
                .map(Db::getId)
                .toList();
    }

    @Override
    public KopisConcertResponse fetchConcertDetail(String concertCode) {
        return kopisConcertClient.fetchConcertDetail(concertCode);
    }
}
