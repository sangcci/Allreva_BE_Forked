package com.backend.allreva.module.concert.hall.application.port;

import com.backend.allreva.module.concert.hall.domain.ConcertHall;

import java.util.List;

public interface ConcertHallDataSyncPort {
    List<ConcertHall> fetchConcertHallDetails(String hallCode);
}
