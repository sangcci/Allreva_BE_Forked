package com.backend.allreva.concert.place.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ConvenienceInfo {

    private boolean hasParkingLot;
    private boolean hasRestaurant;
    private boolean hasCafe;
    private boolean hasStore;
    private boolean hasDisabledParking;
    private boolean hasDisabledToilet;
    private boolean hasElevator;
    private boolean hasRunway;

    @Builder
    private ConvenienceInfo(
            final boolean hasParkingLot,
            final boolean hasRestaurant,
            final boolean hasCafe,
            final boolean hasStore,
            final boolean hasDisabledParking,
            final boolean hasDisabledToilet,
            final boolean hasElevator,
            final boolean hasRunway) {
        this.hasParkingLot = hasParkingLot;
        this.hasRestaurant = hasRestaurant;
        this.hasCafe = hasCafe;
        this.hasStore = hasStore;
        this.hasDisabledParking = hasDisabledParking;
        this.hasDisabledToilet = hasDisabledToilet;
        this.hasElevator = hasElevator;
        this.hasRunway = hasRunway;
    }
}
