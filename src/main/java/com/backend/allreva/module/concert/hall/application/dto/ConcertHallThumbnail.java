package com.backend.allreva.module.concert.hall.application.dto;

import com.backend.allreva.module.concert.hall.domain.ConcertHall;
import com.backend.allreva.module.concert.hall.domain.ConvenienceInfo;
import com.backend.allreva.module.concert.hall.domain.ConcertHallDocument;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record ConcertHallThumbnail(
        String id,
        String name,
        String address,
        int seatScale,
        ConvenienceInfo convenienceInfo
) {
    public static ConcertHallThumbnail from(final ConcertHall concertHall) {
        return new ConcertHallThumbnail(
                concertHall.getId(),
                concertHall.getName(),
                concertHall.getLocation().getAddress(),
                concertHall.getSeatScale(),
                concertHall.getConvenienceInfo()
        );
    }

    public static ConcertHallThumbnail from(final ConcertHallDocument document) {
        log.info("document: {}", document.toString());
        ConvenienceInfo convenienceInfo = ConvenienceInfo.builder()
                .hasParkingLot(document.getParking())
                .hasRestaurant(document.getRestaurant())
                .hasCafe(document.getCafe())
                .hasStore(document.getStore())
                .hasDisabledParking(document.getParkBarrier())
                .hasDisabledToilet(document.getRestBarrier())
                .hasElevator(document.getElevBarrier())
                .hasRunway(document.getRunwBarrier())
                .build();
        return new ConcertHallThumbnail(
                document.getId(),
                document.getName(),
                document.getAddress(),
                document.getSeatScale(),
                convenienceInfo
        );
    }
}
