package com.backend.allreva.module.concert.place.domain;

import com.backend.allreva.module.concert.place.application.dto.ConcertHallDetailResponse;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ConcertHallRepository {

    ConcertHall save(ConcertHall concertHall);

    ConcertHallDetailResponse findDetailByHallCode(String hallCode);

    Optional<ConcertHall> findByHallCode(String hallCode);

    List<String> findAllHallCodes();

    Set<String> findAllFacilityCodes();

    Set<String> findHallCodesByFacilityCode(String facilityCode);

    void deleteAll();
}
