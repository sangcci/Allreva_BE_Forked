package com.backend.allreva.concert.place.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ConcertHall {

    private String hallCode;
    private String name;
    private int seatScale;
    private ConvenienceInfo convenienceInfo;
    private Location location;

    @Builder
    private ConcertHall(
            final String hallCode,
            final String name,
            final int seatScale,
            final ConvenienceInfo convenienceInfo,
            final Location location) {
        this.hallCode = hallCode;
        this.name = name;
        this.seatScale = seatScale;
        this.convenienceInfo = convenienceInfo;
        this.location = location;
    }
}
