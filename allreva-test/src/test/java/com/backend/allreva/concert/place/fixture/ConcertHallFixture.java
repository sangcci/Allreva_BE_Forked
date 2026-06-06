package com.backend.allreva.concert.place.fixture;

import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;

import com.backend.allreva.concert.place.domain.ConcertHall;
import com.backend.allreva.concert.place.domain.Location;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.instancio.Instancio;
import org.instancio.Model;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcertHallFixture {

    public static Model<ConcertHall> concertHallModel() {
        return Instancio.of(ConcertHall.class)
                .generate(allStrings(), gen -> gen.string().maxLength(20))
                .generate(
                        field(Location.class, "longitude"), gen -> gen.doubles().range(124.0, 132.0))
                .generate(
                        field(Location.class, "latitude"), gen -> gen.doubles().range(33.0, 43.0))
                .toModel();
    }
}
