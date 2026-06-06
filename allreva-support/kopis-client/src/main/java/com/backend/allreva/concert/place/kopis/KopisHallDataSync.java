package com.backend.allreva.concert.place.kopis;

import com.backend.allreva.common.kopis.KopisRateLimiter;
import com.backend.allreva.concert.place.command.implementation.ConcertHallDataSyncPort;
import com.backend.allreva.concert.place.domain.ConcertHall;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KopisHallDataSync implements ConcertHallDataSyncPort {
    private final KopisHallClient kopisHallClient;
    private final KopisHallMapper kopisHallMapper;
    private final KopisRateLimiter kopisRateLimiter;

    @Override
    public List<ConcertHall> fetchHalls(final String facilityCode) {
        kopisRateLimiter.acquire();
        KopisHallDetailResponse response = kopisHallClient.fetchFacility(facilityCode);

        return kopisHallMapper.toHalls(response);
    }
}
