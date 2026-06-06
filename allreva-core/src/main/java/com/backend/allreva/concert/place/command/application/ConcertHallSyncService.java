package com.backend.allreva.concert.place.command.application;

import com.backend.allreva.concert.place.command.implementation.ConcertHallRefresher;
import com.backend.allreva.concert.place.command.implementation.ConcertHallRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class ConcertHallSyncService {

    private final ConcertHallRegistry concertHallRegistry;
    private final ConcertHallRefresher concertHallRefresher;

    @CacheEvict(
            cacheNames = {"concertHall"},
            allEntries = true)
    public void sync() {
        for (String facilityCode : concertHallRegistry.facilityCodes()) {
            try {
                concertHallRefresher.refresh(facilityCode);
                log.debug("Hall sync complete for facility: {}", facilityCode);
            } catch (Exception e) {
                log.error("Hall sync failed for facility: {}", facilityCode, e);
            }
        }
    }
}
