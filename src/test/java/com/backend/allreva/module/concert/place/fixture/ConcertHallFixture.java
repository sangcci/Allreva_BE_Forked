package com.backend.allreva.module.concert.place.fixture;

import com.backend.allreva.module.concert.place.domain.ConcertHall;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.instancio.Instancio;
import org.instancio.Model;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcertHallFixture {

    public static Model<ConcertHall> concertHallModel() {
        return Instancio.of(ConcertHall.class).toModel();
    }
}
