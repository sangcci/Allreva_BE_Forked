package com.backend.allreva.module.concert.hall.fixture;

import com.backend.allreva.module.concert.hall.domain.ConcertHall;
import com.backend.allreva.module.concert.hall.domain.ConvenienceInfo;
import com.backend.allreva.module.concert.hall.domain.Location;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcertHallFixture {

    public static ConcertHall createConcertHall(String hallId) {
        return ConcertHall.builder()
                .id(hallId)
                .name("서울 예술의전당 콘서트홀")
                .seatScale(2500)
                .convenienceInfo(ConvenienceInfo.builder()
                        .hasParkingLot(true)
                        .hasRestaurant(true)
                        .hasCafe(true)
                        .hasStore(true)
                        .hasDisabledParking(true)
                        .hasDisabledToilet(true)
                        .hasElevator(true)
                        .hasRunway(true)
                        .build())
                .location(Location.builder()
                        .longitude(127.013079)
                        .latitude(37.518486)
                        .address("서울특별시 서초구 남부순환로 2406")
                        .build())
                .build();
    }

    public static ConcertHall createConcertHallWithStar(String hallId, double star, int reviewCount) {
        ConcertHall hall = createConcertHall(hallId);
        // 별점 업데이트 (starDelta * reviewCount)
        int totalStarScore = (int) (star * reviewCount);
        hall.updateStar(totalStarScore, reviewCount);
        return hall;
    }

    public static ConcertHall createSmallConcertHall(String hallId) {
        return ConcertHall.builder()
                .id(hallId)
                .name("홍대 라이브홀")
                .seatScale(300)
                .convenienceInfo(ConvenienceInfo.builder()
                        .hasParkingLot(false)
                        .hasRestaurant(false)
                        .hasCafe(true)
                        .hasStore(false)
                        .hasDisabledParking(false)
                        .hasDisabledToilet(true)
                        .hasElevator(false)
                        .hasRunway(false)
                        .build())
                .location(Location.builder()
                        .longitude(126.923079)
                        .latitude(37.558486)
                        .address("서울특별시 마포구 와우산로 94")
                        .build())
                .build();
    }
}
