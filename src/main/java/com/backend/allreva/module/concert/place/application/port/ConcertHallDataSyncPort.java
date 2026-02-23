package com.backend.allreva.module.concert.place.application.port;

import com.backend.allreva.module.concert.place.domain.ConcertHall;

import java.util.List;

public interface ConcertHallDataSyncPort {
    List<ConcertHall> fetchConcertHallDetails(String hallCode);
}
