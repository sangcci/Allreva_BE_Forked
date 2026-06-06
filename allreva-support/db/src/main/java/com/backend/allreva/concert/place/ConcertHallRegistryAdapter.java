package com.backend.allreva.concert.place;

import com.backend.allreva.concert.place.command.implementation.ConcertHallRegistryPort;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcertHallRegistryAdapter implements ConcertHallRegistryPort {

    private final ConcertHallJpaRepository jpa;

    @Override
    public List<String> findAllHallCodes() {
        return jpa.findAllHallCodes();
    }

    @Override
    public Set<String> findAllFacilityCodes() {
        return new HashSet<>(jpa.findAllFacilityCodes());
    }

    @Override
    public Set<String> findHallCodesByFacilityCode(final String facilityCode) {
        return new HashSet<>(jpa.findHallCodesByFacilityCode(facilityCode));
    }
}
