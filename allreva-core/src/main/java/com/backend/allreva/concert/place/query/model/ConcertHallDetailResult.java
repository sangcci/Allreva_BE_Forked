package com.backend.allreva.concert.place.query.model;

import com.backend.allreva.concert.place.domain.ConcertHall;
import com.backend.allreva.concert.place.domain.ConvenienceInfo;
import com.backend.allreva.concert.place.domain.Location;

public record ConcertHallDetailResult(
        String name, Integer seatScale, ConvenienceInfo convenienceInfo, Location location) {

    public static ConcertHallDetailResult from(final ConcertHall hall) {
        return new ConcertHallDetailResult(
                hall.getName(), hall.getSeatScale(), hall.getConvenienceInfo(), hall.getLocation());
    }
}
