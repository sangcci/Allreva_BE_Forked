package com.backend.allreva.module.concert.place.domain;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ConcertHallRepository {

    ConcertHall save(ConcertHall concertHall);

    Optional<ConcertHall> findById(String hallCode);

    List<String> findAllHallCodes();

    Set<String> findAllFacilityCodes();

    Set<String> findHallCodesByFacilityCode(String facilityCode);

    void deleteAll();
}
