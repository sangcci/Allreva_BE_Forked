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

    public static Concert createConcert(Long concertId) {
        Concert concert = Concert.builder()
                .code(Code.builder()
                        .hallCode("FC001")
                        .concertCode("PF001")
                        .build())
                .concertInfo(ConcertInfo.builder()
                        .title("아이유 콘서트")
                        .host("카카오엔터테인먼트")
                        .price("R석 154,000원, S석 132,000원")
                        .performStatus(ConcertStatus.IN_PROGRESS)
                        .dateInfo(DateInfo.builder()
                                .startDate(LocalDate.of(2030, 12, 1))
                                .endDate(LocalDate.of(2030, 12, 31))
                                .timeTable("화,수,목,금(20:00),토,일(14:00,19:00)")
                                .build())
                        .build())
                .poster(new Image("https://example.com/poster.jpg"))
                .detailImages(List.of(
                        new Image("https://example.com/detail1.jpg"),
                        new Image("https://example.com/detail2.jpg")
                ))
                .sellers(Set.of(
                        Seller.builder()
                                .name("인터파크티켓")
                                .salesUrl("https://ticket.interpark.com")
                                .build(),
                        Seller.builder()
                                .name("예스24티켓")
                                .salesUrl("https://ticket.yes24.com")
                                .build()
                ))
                .episodes(List.of("Episode 1", "Episode 2"))
                .build();

        ReflectionTestUtils.setField(concert, "id", concertId);
        ReflectionTestUtils.setField(concert, "viewCount", 100L);
        return concert;
    }

    public static Concert createConcertWithCustomTitle(Long concertId, String title) {
        Concert concert = createConcert(concertId);
        ConcertInfo concertInfo = ConcertInfo.builder()
                .title(title)
                .host("카카오엔터테인먼트")
                .price("R석 154,000원")
                .performStatus(ConcertStatus.IN_PROGRESS)
                .dateInfo(DateInfo.builder()
                        .startDate(LocalDate.of(2030, 12, 1))
                        .endDate(LocalDate.of(2030, 12, 31))
                        .timeTable("화,수,목,금(20:00)")
                        .build())
                .build();

        concert.updateFrom(
                concert.getCode(),
                concertInfo,
                concert.getEpisodes(),
                concert.getPoster(),
                concert.getDetailImages(),
                concert.getSellers()
        );
        return concert;
    }

    /**
     * hallCode를 지정하여 Concert를 생성
     */
    public static Concert createConcertWithHallCode(String hallCode) {
        return Concert.builder()
                .code(Code.builder()
                        .hallCode(hallCode)
                        .concertCode("concertCode")
                        .build())
                .concertInfo(ConcertInfo.builder()
                        .title("Sample Concert")
                        .price("R석 100,000원")
                        .performStatus(ConcertStatus.IN_PROGRESS)
                        .host("Sample Host")
                        .dateInfo(DateInfo.builder()
                                .startDate(LocalDate.of(2030, 12, 1))
                                .endDate(LocalDate.of(2030, 12, 31))
                                .timeTable("화,수,목,금(20:00)")
                                .build())
                        .build())
                .poster(new Image("http://example.com/poster.jpg"))
                .detailImages(List.of(
                        new Image("http://example.com/detail1.jpg"),
                        new Image("http://example.com/detail2.jpg")
                ))
                .sellers(Set.of(
                        Seller.builder()
                                .name("Sample Seller")
                                .salesUrl("http://seller.com")
                                .build()
                ))
                .episodes(List.of("Episode 1"))
                .build();
    }
}
