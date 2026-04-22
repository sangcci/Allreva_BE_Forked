package com.backend.allreva.module.concert.place.application;

import com.backend.allreva.module.concert.place.application.port.ConcertHallDataSyncPort;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import java.util.List;
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

    /** 공연장 정보 매주 동기화 — 매주 일요일 새벽 2시 */
    @Scheduled(cron = "0 0 2 * * SUN")
    public void fetchWeeklyHallInfoList() {
        try {
            fetchConcertHallInfoList();
            log.info("Weekly concert hall info update complete");
        } catch (Exception e) {
            log.error("Can't update weekly hall info. Message: {}", e.getMessage());
        }
    }

    @CacheEvict(cacheNames = "placeMain", allEntries = true)
    public void fetchConcertHallInfoList() {
        List<String> hallIds = concertHallRepository.findAllIds();

        for (String hallId : hallIds) {
            try {
                List<ConcertHall> halls = concertHallDataSyncPort.fetchConcertHallDetails(getFacilityCode(hallId));
                halls.stream().filter(hall -> hall.getId().equals(hallId)).forEach(concertHallRepository::save);
                log.debug("Hall detail fetch complete: {}", hallId);
                Thread.sleep(KOPIS_RATE_LIMIT_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Hall sync interrupted: {}", hallId);
                break;
            } catch (Exception e) {
                log.error("Hall sync failed for {}: {}", hallId, e.getMessage());
            }
        }
    }

    private String getFacilityCode(final String hallId) {
        return hallId.split("-")[0];
    }
}
