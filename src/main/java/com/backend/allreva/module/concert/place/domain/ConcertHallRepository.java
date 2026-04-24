package com.backend.allreva.module.concert.place.domain;

import com.backend.allreva.module.concert.place.application.dto.ConcertHallDetailResponse;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ConcertHallRepository {

    ConcertHall save(ConcertHall concertHall);

    Optional<ConcertHall> findByIdWithLock(String hallId);

    ConcertHallDetailResponse findDetailByHallCode(String hallCode);

    Optional<ConcertHall> findById(String id);

    List<String> findAllIds();

    Set<String> findAllFacilityCodes();

    Set<String> findIdsByFacilityCode(String facilityCode);

    void deleteAll();
}
