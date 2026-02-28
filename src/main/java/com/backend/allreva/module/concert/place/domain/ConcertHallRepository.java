package com.backend.allreva.module.concert.place.domain;

import com.backend.allreva.module.concert.place.application.dto.ConcertHallDetailResponse;
import java.util.Optional;

public interface ConcertHallRepository {

    ConcertHall save(ConcertHall concertHall);

    Optional<ConcertHall> findByIdWithLock(String hallId);

    ConcertHallDetailResponse findDetailByHallCode(String hallCode);

    Optional<ConcertHall> findById(String id);

    void deleteAll();
}
