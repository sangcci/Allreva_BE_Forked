package com.backend.allreva.module.concert.place.application.dto;

import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.value.ConvenienceInfo;

public record ConcertHallThumbnail(
        String id, String name, String address, int seatScale, ConvenienceInfo convenienceInfo) {
    public static ConcertHallThumbnail from(final ConcertHall concertHall) {
        return new ConcertHallThumbnail(
                concertHall.getId(),
                concertHall.getName(),
                concertHall.getLocation().getAddress(),
                concertHall.getSeatScale(),
                concertHall.getConvenienceInfo());
    }
}
