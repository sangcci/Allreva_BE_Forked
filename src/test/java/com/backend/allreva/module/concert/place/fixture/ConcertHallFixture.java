package com.backend.allreva.module.concert.place.fixture;

import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.value.ConvenienceInfo;
import com.backend.allreva.module.concert.place.domain.value.Location;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcertHallFixture {

    public static ConcertHall createTestConcertHall() {
        return ConcertHall.builder()
                .hallCode("hall-001")
                .name("서울 예술의전당")
                .seatScale(2500)
                .convenienceInfo(ConvenienceInfo.builder()
                        .hasParkingLot(true)
                        .hasRestaurant(true)
                        .hasCafe(true)
                        .hasDisabledParking(true)
                        .build())
                .location(Location.builder()
                        .longitude(127.013079)
                        .latitude(37.518486)
                        .address("서울특별시 송파구 올림픽로 424")
                        .build())
                .build();
    }

    public static ConcertHall createConcertHall(String hallCode) {
        return ConcertHall.builder()
                .hallCode(hallCode)
                .name("서울 예술의전당")
                .seatScale(2500)
                .convenienceInfo(ConvenienceInfo.builder()
                        .hasParkingLot(true)
                        .hasRestaurant(true)
                        .hasCafe(true)
                        .hasDisabledParking(true)
                        .build())
                .location(Location.builder()
                        .longitude(127.013079)
                        .latitude(37.518486)
                        .address("서울특별시 송파구 올림픽로 424")
                        .build())
                .build();
    }

}
