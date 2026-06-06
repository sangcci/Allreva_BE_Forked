package com.backend.allreva.concert.place.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Location {

    private Double longitude;
    private Double latitude;
    private String address;

    @Builder
    private Location(final Double longitude, final Double latitude, final String address) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.address = address;
    }
}
