package com.backend.allreva.module.concert.place.infra.kopis;

import com.backend.allreva.module.concert.place.application.port.ConcertHallDataSyncPort;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Kopis Hall API Adapter
 *
 * <p>Kopis API를 호출하여 공연장 정보를 조회하고 도메인 객체로 변환합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KopisHallDataSync implements ConcertHallDataSyncPort {
    private final KopisHallClient kopisHallClient;

    @Override
    public List<ConcertHall> fetchConcertHallDetails(String hallCode) {
        KopisHallResponse response = kopisHallClient.fetchConcertHallDetail(hallCode);

        List<ConcertHall> concertHalls = new ArrayList<>();
        int hallCount = response.getDb().getMt13s().getMt13List().size();

        for (int i = 0; i < hallCount; i++) {
            concertHalls.add(KopisHallResponse.toEntity(response, i));
        }

        return concertHalls;
    }
}
