package com.backend.allreva.module.concert.place.fixture;

import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.value.ConvenienceInfo;
import com.backend.allreva.module.concert.place.domain.value.Location;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.test.util.ReflectionTestUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcertHallFixture {

    public static ConcertHall createTestConcertHall() {
        return ConcertHall.builder()
                .id("hall-001")
                .name("서울 예술의전당")
                .seatScale(2500)
                .convenienceInfo(
                        ConvenienceInfo.builder()
                                .hasParkingLot(true)
                                .hasRestaurant(true)
                                .hasCafe(true)
                                .hasDisabledParking(true)
                                .build()
                )
                .location(
                        Location.builder()
                                .longitude(127.013079)
                                .latitude(37.518486)
                                .address("서울특별시 송파구 올림픽로 424")
                                .build()
                )
                .build();
    }

    public static ConcertHall createConcertHall(String id) {
        return ConcertHall.builder()
                .id(id)
                .name("서울 예술의전당")
                .seatScale(2500)
                .convenienceInfo(
                        ConvenienceInfo.builder()
                                .hasParkingLot(true)
                                .hasRestaurant(true)
                                .hasCafe(true)
                                .hasDisabledParking(true)
                                .build()
                )
                .location(
                        Location.builder()
                                .longitude(127.013079)
                                .latitude(37.518486)
                                .address("서울특별시 송파구 올림픽로 424")
                                .build()
                )
                .build();
    }

    public static ConcertHall createConcertHallWithStar(String id, double star, int reviewCount) {
        ConcertHall concertHall = ConcertHall.builder()
                .id(id)
                .name("서울 예술의전당")
                .seatScale(2500)
                .convenienceInfo(
                        ConvenienceInfo.builder()
                                .hasParkingLot(true)
                                .hasRestaurant(true)
                                .hasCafe(true)
                                .hasDisabledParking(true)
                                .build()
                )
                .location(
                        Location.builder()
                                .longitude(127.013079)
                                .latitude(37.518486)
                                .address("서울특별시 송파구 올림픽로 424")
                                .build()
                )
                .build();
        ReflectionTestUtils.setField(concertHall, "star", star);
        ReflectionTestUtils.setField(concertHall, "reviewCount", (long) reviewCount);
        ReflectionTestUtils.setField(concertHall, "totalStars", (long) (star * reviewCount));
        return concertHall;
    }
}
