package com.backend.allreva.concert.place.fixture;

import static org.instancio.Select.field;

import com.backend.allreva.concert.place.domain.ConcertHall;
import com.backend.allreva.concert.place.domain.ConvenienceInfo;
import com.backend.allreva.concert.place.domain.Location;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.instancio.Instancio;
import org.instancio.Model;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcertHallFixture {

    public static final String HALL_CODE = "HALL_TEST_001";
    public static final String NAME = "테스트 홀";
    public static final String ADDRESS = "서울시 테스트구";

    public static Model<ConcertHall> concertHallModel() {
        return Instancio.of(ConcertHall.class)
                .set(field(ConcertHall.class, "hallCode"), HALL_CODE)
                .set(field(ConcertHall.class, "name"), NAME)
                .set(field(ConcertHall.class, "seatScale"), 1000)
                .set(field(ConvenienceInfo.class, "hasParkingLot"), true)
                .set(field(ConvenienceInfo.class, "hasRestaurant"), true)
                .set(field(ConvenienceInfo.class, "hasCafe"), false)
                .set(field(ConvenienceInfo.class, "hasStore"), true)
                .set(field(ConvenienceInfo.class, "hasDisabledParking"), false)
                .set(field(ConvenienceInfo.class, "hasDisabledToilet"), true)
                .set(field(ConvenienceInfo.class, "hasElevator"), true)
                .set(field(ConvenienceInfo.class, "hasRunway"), false)
                .set(field(Location.class, "longitude"), 127.1)
                .set(field(Location.class, "latitude"), 37.5)
                .set(field(Location.class, "address"), ADDRESS)
                .toModel();
    }

    public static ConcertHall createConcertHall() {
        return Instancio.create(concertHallModel());
    }
}
