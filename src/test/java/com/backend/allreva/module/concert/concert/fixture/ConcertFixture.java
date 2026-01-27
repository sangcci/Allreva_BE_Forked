package com.backend.allreva.module.concert.concert.fixture;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.Code;
import com.backend.allreva.module.concert.concert.domain.ConcertInfo;
import com.backend.allreva.module.concert.concert.domain.ConcertStatus;
import com.backend.allreva.module.concert.concert.domain.DateInfo;
import com.backend.allreva.module.concert.concert.domain.Seller;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcertFixture {

    public static Concert createTestConcert() {
        return Concert.builder()
                .code(Code.builder()
                        .hallCode("123")
                        .concertCode("456")
                        .build())
                .concertInfo(ConcertInfo.builder()
                        .title("Sample Concert")
                        .price("price")
                        .performStatus(ConcertStatus.IN_PROGRESS)
                        .host("host")
                        .dateInfo(DateInfo.builder()
                                .startDate(LocalDate.of(2030, 12, 1))
                                .endDate(LocalDate.of(2030, 12, 2))
                                .timeTable("timetable")
                                .build())
                        .build())
                .poster(new Image("http://example.com/poster.jpg"))
                .detailImages(List.of(new Image("http://example.com/detail1.jpg"), new Image("http://example.com/detail2.jpg")))
                .sellers(Set.of(Seller.builder()
                        .name("Sample Seller")
                        .salesUrl("http://seller.com")
                        .build()))
                .build();
    }

    public static Concert createConcertWithHallCode(String hallCode) {
        return Concert.builder()
                .code(Code.builder()
                        .hallCode(hallCode)
                        .concertCode("456")
                        .build())
                .concertInfo(ConcertInfo.builder()
                        .title("Sample Concert")
                        .price("price")
                        .performStatus(ConcertStatus.IN_PROGRESS)
                        .host("host")
                        .dateInfo(DateInfo.builder()
                                .startDate(LocalDate.of(2030, 12, 1))
                                .endDate(LocalDate.of(2030, 12, 2))
                                .timeTable("timetable")
                                .build())
                        .build())
                .poster(new Image("http://example.com/poster.jpg"))
                .detailImages(List.of(new Image("http://example.com/detail1.jpg"), new Image("http://example.com/detail2.jpg")))
                .sellers(Set.of(Seller.builder()
                        .name("Sample Seller")
                        .salesUrl("http://seller.com")
                        .build()))
                .build();
    }

    public static Concert createConcert(Long id) {
        Concert concert = Concert.builder()
                .code(Code.builder()
                        .hallCode("123")
                        .concertCode("456")
                        .build())
                .concertInfo(ConcertInfo.builder()
                        .title("Sample Concert")
                        .price("price")
                        .performStatus(ConcertStatus.IN_PROGRESS)
                        .host("host")
                        .dateInfo(DateInfo.builder()
                                .startDate(LocalDate.of(2030, 12, 1))
                                .endDate(LocalDate.of(2030, 12, 2))
                                .timeTable("timetable")
                                .build())
                        .build())
                .poster(new Image("http://example.com/poster.jpg"))
                .detailImages(List.of(new Image("http://example.com/detail1.jpg"), new Image("http://example.com/detail2.jpg")))
                .sellers(Set.of(Seller.builder()
                        .name("Sample Seller")
                        .salesUrl("http://seller.com")
                        .build()))
                .build();
        ReflectionTestUtils.setField(concert, "id", id);
        return concert;
    }
}
