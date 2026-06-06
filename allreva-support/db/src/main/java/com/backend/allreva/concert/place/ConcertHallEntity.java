package com.backend.allreva.concert.place;

import com.backend.allreva.concert.place.domain.ConcertHall;
import com.backend.allreva.concert.place.domain.ConvenienceInfo;
import com.backend.allreva.concert.place.domain.Location;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "ConcertHall")
public class ConcertHallEntity {

    @Id
    private String hallCode;

    @Column(nullable = false)
    private String name;

    private int seatScale;

    @Column(name = "parking")
    private boolean hasParkingLot;

    @Column(name = "restaurant")
    private boolean hasRestaurant;

    @Column(name = "cafe")
    private boolean hasCafe;

    @Column(name = "store")
    private boolean hasStore;

    @Column(name = "park_barrier")
    private boolean hasDisabledParking;

    @Column(name = "rest_barrier")
    private boolean hasDisabledToilet;

    @Column(name = "elev_barrier")
    private boolean hasElevator;

    @Column(name = "runw_barrier")
    private boolean hasRunway;

    private Double longitude;
    private Double latitude;

    @Column(nullable = false)
    private String address;

    private ConcertHallEntity(
            final String hallCode,
            final String name,
            final int seatScale,
            final ConvenienceInfo convenienceInfo,
            final Location location) {
        this.hallCode = hallCode;
        this.name = name;
        this.seatScale = seatScale;
        if (convenienceInfo != null) {
            this.hasParkingLot = convenienceInfo.isHasParkingLot();
            this.hasRestaurant = convenienceInfo.isHasRestaurant();
            this.hasCafe = convenienceInfo.isHasCafe();
            this.hasStore = convenienceInfo.isHasStore();
            this.hasDisabledParking = convenienceInfo.isHasDisabledParking();
            this.hasDisabledToilet = convenienceInfo.isHasDisabledToilet();
            this.hasElevator = convenienceInfo.isHasElevator();
            this.hasRunway = convenienceInfo.isHasRunway();
        }
        if (location != null) {
            this.longitude = location.getLongitude();
            this.latitude = location.getLatitude();
            this.address = location.getAddress();
        }
    }

    public static ConcertHallEntity from(final ConcertHall hall) {
        return new ConcertHallEntity(
                hall.getHallCode(), hall.getName(), hall.getSeatScale(), hall.getConvenienceInfo(), hall.getLocation());
    }

    public ConcertHall toDomain() {
        return ConcertHall.builder()
                .hallCode(hallCode)
                .name(name)
                .seatScale(seatScale)
                .convenienceInfo(ConvenienceInfo.builder()
                        .hasParkingLot(hasParkingLot)
                        .hasRestaurant(hasRestaurant)
                        .hasCafe(hasCafe)
                        .hasStore(hasStore)
                        .hasDisabledParking(hasDisabledParking)
                        .hasDisabledToilet(hasDisabledToilet)
                        .hasElevator(hasElevator)
                        .hasRunway(hasRunway)
                        .build())
                .location(Location.builder()
                        .longitude(longitude)
                        .latitude(latitude)
                        .address(address)
                        .build())
                .build();
    }
}
