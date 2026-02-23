package com.backend.allreva.module.search.domain;

import com.backend.allreva.module.concert.hall.application.dto.ConcertHallMainResponse;

public interface ConcertHallSearchRepository {

    ConcertHallMainResponse searchMain(String address, int minSeatSize, String cursorId, int pageSize);
}
