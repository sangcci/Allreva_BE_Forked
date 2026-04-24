package com.backend.allreva.module.concert.place.application;

import com.backend.allreva.module.concert.place.application.port.ConcertHallDataSyncPort;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class ConcertHallSyncScheduler {

    private static final int KOPIS_RATE_LIMIT_MILLIS = 100;

    private final ConcertHallDataSyncPort concertHallDataSyncPort;
    private final ConcertHallRepository concertHallRepository;

    /** 공연장 정보 매월 동기화 — 매월 1일 새벽 2시 */
    @Scheduled(cron = "0 0 2 1 * *")
    public void fetchMonthlyHallInfoList() {
        try {
            fetchConcertHallInfoList();
            log.info("Monthly concert hall info update complete");
        } catch (Exception e) {
            log.error("Can't update monthly hall info. Message: {}", e.getMessage());
        }
    }

    @CacheEvict(cacheNames = "placeMain", allEntries = true)
    public void fetchConcertHallInfoList() {
        Set<String> facilityCodes = concertHallRepository.findAllFacilityCodes();

        for (String facilityCode : facilityCodes) {
            try {
                List<ConcertHall> halls = concertHallDataSyncPort.fetchConcertHallDetails(facilityCode);
                Set<String> existingIds = concertHallRepository.findIdsByFacilityCode(facilityCode);

                for (ConcertHall hall : halls) {
                    // Save only if hallId exists in whitelist (already in DB)
                    if (existingIds.contains(hall.getId())) {
                        concertHallRepository.save(hall);
                    } else {
                        log.debug("Skipping non-whitelisted hall: {}", hall.getId());
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
