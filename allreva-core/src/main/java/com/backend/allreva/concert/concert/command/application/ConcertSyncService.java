package com.backend.allreva.concert.concert.command.application;

import com.backend.allreva.concert.concert.command.implementation.ConcertRefresher;
import com.backend.allreva.concert.concert.command.implementation.ConcertSyncRegistry;
import com.backend.allreva.concert.concert.domain.ConcertSyncPeriod;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class ConcertSyncService {

    private final ConcertSyncRegistry concertSyncRegistry;
    private final ConcertRefresher concertRefresher;

    @CacheEvict(
            cacheNames = {"concertMain", "concertSearch", "concertRelated"},
            allEntries = true)
    public void sync(final LocalDate today) {
        ConcertSyncPeriod period = ConcertSyncPeriod.from(today);

        for (String hallCode : concertSyncRegistry.hallCodes()) {
            try {
                concertRefresher.refresh(hallCode, period);
                log.debug("Concert sync complete for hall: {}", hallCode);
            } catch (Exception e) {
                log.error("Concert sync failed for hall {}: {}", hallCode, e.getMessage());
            }
        }
    }
}
