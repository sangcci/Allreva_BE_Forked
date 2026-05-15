package com.backend.allreva.module.concert.place.application;

import com.backend.allreva.module.concert.place.application.port.ConcertHallDataSyncPort;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class ConcertHallSyncService {

    private static final int KOPIS_RATE_LIMIT_MILLIS = 100;

    private final ConcertHallDataSyncPort concertHallDataSyncPort;
    private final ConcertHallRepository concertHallRepository;

    @CacheEvict(
            cacheNames = {"concertHall"},
            allEntries = true)
    public void fetchConcertHallInfoList() {
        Set<String> facilityCodes = concertHallRepository.findAllFacilityCodes();

        for (String facilityCode : facilityCodes) {
            try {
                List<ConcertHall> halls = concertHallDataSyncPort.fetchConcertHallDetails(facilityCode);
                Set<String> existingHallCodes = concertHallRepository.findHallCodesByFacilityCode(facilityCode);

                for (ConcertHall hall : halls) {
                    if (existingHallCodes.contains(hall.getHallCode())) {
                        concertHallRepository.save(hall);
                    } else {
                        log.debug("Skipping non-whitelisted hall: {}", hall.getHallCode());
                    }
                }
                log.debug("Hall detail fetch complete for facility: {}", facilityCode);
                Thread.sleep(KOPIS_RATE_LIMIT_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Hall sync interrupted for facility: {}", facilityCode);
                break;
            } catch (Exception e) {
                log.error("Hall sync failed for facility {}: {}", facilityCode, e.getMessage());
            }
        }
    }
}
