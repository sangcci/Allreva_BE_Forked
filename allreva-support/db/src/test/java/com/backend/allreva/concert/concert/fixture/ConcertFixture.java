package com.backend.allreva.concert.concert.fixture;

import static org.instancio.Select.field;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.concert.concert.domain.Concert;
import com.backend.allreva.concert.concert.domain.ConcertInfo;
import com.backend.allreva.concert.concert.domain.ConcertStatus;
import com.backend.allreva.concert.concert.domain.DateInfo;
import com.backend.allreva.concert.concert.domain.Seller;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.instancio.Instancio;
import org.instancio.Model;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcertFixture {

    public static final String CONCERT_CODE = "PF_TEST_001";
    public static final String HALL_CODE = "HALL_TEST_001";
    public static final String TITLE = "테스트 공연";
    public static final String POSTER_URL = "poster.jpg";

    public static Model<Concert> concertModel() {
        return Instancio.of(Concert.class)
                .set(field(Concert.class, "concertCode"), CONCERT_CODE)
                .set(field(Concert.class, "hallCode"), HALL_CODE)
                .set(field(ConcertInfo.class, "title"), TITLE)
                .set(field(ConcertInfo.class, "price"), "10000")
                .set(field(ConcertInfo.class, "performStatus"), ConcertStatus.SCHEDULED)
                .set(field(ConcertInfo.class, "host"), "주최사")
                .set(field(DateInfo.class, "startDate"), LocalDate.of(2030, 1, 1))
                .set(field(DateInfo.class, "endDate"), LocalDate.of(2030, 1, 2))
                .set(field(DateInfo.class, "timeTable"), "19:00")
                .set(field(Concert.class, "poster"), new Image(POSTER_URL))
                .set(field(Concert.class, "detailImages"), List.of(new Image("detail-1.jpg")))
                .set(
                        field(Concert.class, "sellers"),
                        Set.of(Seller.builder()
                                .name("예매처")
                                .salesUrl("https://ticket.example.com")
                                .build()))
                .set(field(Concert.class, "castNames"), List.of("배우1"))
                .toModel();
    }

    public static Concert createConcert() {
        return Instancio.create(concertModel());
    }
}
