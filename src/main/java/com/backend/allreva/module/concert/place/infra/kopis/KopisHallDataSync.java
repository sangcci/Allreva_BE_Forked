package com.backend.allreva.module.concert.place.infra.kopis;

import com.backend.allreva.module.concert.place.application.port.ConcertHallDataSyncPort;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KopisHallDataSync implements ConcertHallDataSyncPort {
    private final KopisHallClient kopisHallClient;

    @Override
    public List<ConcertHall> fetchConcertHallDetails(String hallCode) {
        KopisHallDetailResponse response = kopisHallClient.fetchConcertHallDetail(hallCode);

        List<ConcertHall> concertHalls = new ArrayList<>();
        int hallCount = response.getDb().getMt13s().getMt13List().size();

        for (int i = 0; i < hallCount; i++) {
            concertHalls.add(KopisHallDetailResponse.toEntity(response, i));
        }

        return concertHalls;
    }
}
