package com.backend.allreva.module.concert.place.application;

import com.backend.allreva.common.util.CsvUtil;
import com.backend.allreva.module.concert.place.application.port.ConcertHallDataSyncPort;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConcertHallSyncScheduler {

    private final ConcertHallDataSyncPort concertHallDataSyncPort;
    private final ConcertHallRepository concertHallRepository;

    /**
     * 공연장 매주 동기화
     * 매주 일요일 새벽 2시
     */
    @Scheduled(cron = "0 0 2 * * SUN") // 매주 일요일 새벽 2시
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
        List<String> hallCodes = CsvUtil.readConcertHallCodes();
        hallCodes.parallelStream()
                .forEach(hallCode -> {
                    List<ConcertHall> concertHalls = concertHallDataSyncPort.fetchConcertHallDetails(getFacilityCode(hallCode));

                    concertHalls.stream()
                            .filter(hall -> hall.getId().equals(hallCode))
                            .forEach(concertHallRepository::save);

                    log.info("hall detail fetch complete for hall Code: {}", hallCode);
                });
    }

    private String getFacilityCode(final String hallCode) {
        return hallCode.split("-")[0];
    }
}
