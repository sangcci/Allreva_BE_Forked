package com.backend.allreva.concert.place.domain;

import java.util.Optional;

public interface ConcertHallRepository {

    Optional<ConcertHall> findById(String hallCode);

    ConcertHall save(ConcertHall concertHall);

    void deleteAll();
}
