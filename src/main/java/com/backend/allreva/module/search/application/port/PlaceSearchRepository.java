package com.backend.allreva.module.search.application.port;

import com.backend.allreva.module.concert.place.application.dto.ConcertHallMainResponse;

public interface PlaceSearchRepository {

    ConcertHallMainResponse searchMain(String address, int minSeatSize, String cursorId, int pageSize);
}
