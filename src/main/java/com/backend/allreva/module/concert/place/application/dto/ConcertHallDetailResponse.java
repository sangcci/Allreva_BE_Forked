package com.backend.allreva.module.concert.place.application.dto;

import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.value.ConvenienceInfo;
import com.backend.allreva.module.concert.place.domain.value.Location;

public record ConcertHallDetailResponse(
        String name, Integer seatScale, ConvenienceInfo convenienceInfo, Location location) {

    public static ConcertHallDetailResponse from(final ConcertHall hall) {
        return new ConcertHallDetailResponse(
                hall.getName(), hall.getSeatScale(), hall.getConvenienceInfo(), hall.getLocation());
    }
}
