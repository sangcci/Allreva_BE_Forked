package com.backend.allreva.module.concert.hall.application.dto;

import com.backend.allreva.module.concert.hall.domain.ConvenienceInfo;
import com.backend.allreva.module.concert.hall.domain.Location;

public record ConcertHallDetailResponse(
        String name,
        Integer seatScale,
        Double star,

        ConvenienceInfo convenienceInfo,
        Location location
) {

}
