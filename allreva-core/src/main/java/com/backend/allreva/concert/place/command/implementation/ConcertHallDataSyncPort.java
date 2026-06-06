package com.backend.allreva.concert.place.command.implementation;

import com.backend.allreva.concert.place.domain.ConcertHall;
import java.util.List;

public interface ConcertHallDataSyncPort {
    List<ConcertHall> fetchHalls(String facilityCode);
}
