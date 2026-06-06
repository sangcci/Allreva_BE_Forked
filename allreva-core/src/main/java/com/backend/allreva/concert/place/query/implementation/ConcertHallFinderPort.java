package com.backend.allreva.concert.place.query.implementation;

import com.backend.allreva.concert.place.query.model.ConcertHallDetailResult;
import java.util.Optional;

public interface ConcertHallFinderPort {

    Optional<ConcertHallDetailResult> findConcertHallDetail(String hallCode);
}
