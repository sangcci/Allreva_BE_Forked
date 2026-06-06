package com.backend.allreva.concert.place.command.implementation;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcertHallRegistry {

    private final ConcertHallRegistryPort concertHallRegistryPort;

    public Set<String> facilityCodes() {
        return concertHallRegistryPort.findAllFacilityCodes();
    }

    public Set<String> hallCodes(final String facilityCode) {
        return concertHallRegistryPort.findHallCodesByFacilityCode(facilityCode);
    }
}
