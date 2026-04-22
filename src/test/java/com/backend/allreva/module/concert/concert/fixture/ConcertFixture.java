package com.backend.allreva.module.concert.concert.fixture;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.value.Code;
import com.backend.allreva.module.concert.concert.domain.value.ConcertInfo;
import com.backend.allreva.module.concert.concert.domain.value.ConcertStatus;
import com.backend.allreva.module.concert.concert.domain.value.DateInfo;
import com.backend.allreva.module.concert.concert.domain.value.Seller;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.test.util.ReflectionTestUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcertFixture {

    public static Concert createTestConcert() {
        return Concert.builder()
                .code(Code.builder().hallCode("123").concertCode("456").build())
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
                .detailImages(List.of(
                        new Image("http://example.com/detail1.jpg"), new Image("http://example.com/detail2.jpg")))
                .sellers(Set.of(Seller.builder()
                        .name("Sample Seller")
                        .salesUrl("http://seller.com")
                        .build()))
                .build();
    }

    public static Concert createConcertWithHallCode(String hallCode) {
        return Concert.builder()
                .code(Code.builder().hallCode(hallCode).concertCode("456").build())
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
                .detailImages(List.of(
                        new Image("http://example.com/detail1.jpg"), new Image("http://example.com/detail2.jpg")))
                .sellers(Set.of(Seller.builder()
                        .name("Sample Seller")
                        .salesUrl("http://seller.com")
                        .build()))
                .build();
    }

    public static Concert createConcert(Long id) {
        Concert concert = Concert.builder()
                .code(Code.builder().hallCode("123").concertCode("456").build())
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
                .detailImages(List.of(
                        new Image("http://example.com/detail1.jpg"), new Image("http://example.com/detail2.jpg")))
                .sellers(Set.of(Seller.builder()
                        .name("Sample Seller")
                        .salesUrl("http://seller.com")
                        .build()))
                .build();
        ReflectionTestUtils.setField(concert, "id", id);
        return concert;
    }

    public static Concert createCompletedConcert(final String concertCode) {
        return Concert.builder()
                .code(Code.builder()
                        .concertCode(concertCode)
                        .hallCode("FC001114-1")
                        .build())
                .concertInfo(ConcertInfo.builder()
                        .title("종료된 공연")
                        .performStatus(ConcertStatus.COMPLETED)
                        .dateInfo(DateInfo.builder()
                                .startDate(LocalDate.of(2025, 1, 1))
                                .endDate(LocalDate.of(2025, 3, 1))
                                .timeTable("토 19:00")
                                .build())
                        .host("소속사")
                        .price("VIP 150,000원")
                        .build())
                .episodes(List.of())
                .poster(new Image("https://poster.jpg"))
                .detailImages(List.of())
                .sellers(Set.of())
                .build();
    }

    public static Concert createInProgressConcert(final String concertCode) {
        return Concert.builder()
                .code(Code.builder()
                        .concertCode(concertCode)
                        .hallCode("FC001114-1")
                        .build())
                .concertInfo(ConcertInfo.builder()
                        .title("진행 중인 공연")
                        .performStatus(ConcertStatus.IN_PROGRESS)
                        .dateInfo(DateInfo.builder()
                                .startDate(LocalDate.now().minusDays(10))
                                .endDate(LocalDate.now().plusDays(10))
                                .timeTable("토 19:00")
                                .build())
                        .host("소속사")
                        .price("VIP 150,000원")
                        .build())
                .episodes(List.of())
                .poster(new Image("https://poster.jpg"))
                .detailImages(List.of())
                .sellers(Set.of())
                .build();
    }
}
