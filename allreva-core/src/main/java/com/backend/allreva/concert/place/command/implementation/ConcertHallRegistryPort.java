package com.backend.allreva.concert.place.command.implementation;

import java.util.List;
import java.util.Set;

public interface ConcertHallRegistryPort {

    List<String> findAllHallCodes();

    Set<String> findAllFacilityCodes();

    Set<String> findHallCodesByFacilityCode(String facilityCode);
}
