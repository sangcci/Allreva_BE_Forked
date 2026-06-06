package com.backend.allreva.concert.place.command.implementation;

import com.backend.allreva.concert.place.domain.ConcertHall;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConcertHallRefresher {

    private final ConcertHallDataSyncPort concertHallDataSyncPort;
    private final ConcertHallRegistry concertHallRegistry;
    private final ConcertHallWriter concertHallWriter;

    public void refresh(final String facilityCode) {
        Set<String> registeredHallCodes = concertHallRegistry.hallCodes(facilityCode);

        for (ConcertHall hall : concertHallDataSyncPort.fetchHalls(facilityCode)) {
            if (registeredHallCodes.contains(hall.getHallCode())) {
                concertHallWriter.upsert(hall);
            } else {
                log.debug("Skipping unregistered hall: {}", hall.getHallCode());
            }
        }
    }
}
