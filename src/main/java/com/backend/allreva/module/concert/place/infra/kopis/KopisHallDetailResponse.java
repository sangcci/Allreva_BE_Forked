package com.backend.allreva.module.concert.place.infra.kopis;

import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.value.ConvenienceInfo;
import com.backend.allreva.module.concert.place.domain.value.Location;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "dbs")
public class KopisHallDetailResponse {

    @JacksonXmlProperty(localName = "db")
    private Db db;

    @Getter
    @NoArgsConstructor
    public static class Db {
        @JacksonXmlProperty(localName = "fcltynm")
        private String fcltynm; // 공연시설명

        @JacksonXmlProperty(localName = "adres")
        private String adres; // 주소

        @JacksonXmlProperty(localName = "la")
        private String la; // 위도

        @JacksonXmlProperty(localName = "lo")
        private String lo; // 경도

        @JacksonXmlProperty(localName = "restaurant")
        private String restaurant; // 식당 여부

        @JacksonXmlProperty(localName = "cafe")
        private String cafe; // 카페 여부

        @JacksonXmlProperty(localName = "store")
        private String store; // 편의점 여부

        @JacksonXmlProperty(localName = "parkbarrier")
        private String parkbarrier; // 장애인 주차장 여부

        @JacksonXmlProperty(localName = "restbarrier")
        private String restbarrier; // 장애인 화장실 여부

        @JacksonXmlProperty(localName = "runwbarrier")
        private String runwbarrier; // 장애인 경사로 여부

        @JacksonXmlProperty(localName = "elevbarrier")
        private String elevbarrier; // 장애인 엘리베이터 여부

        @JacksonXmlProperty(localName = "parkinglot")
        private String parkinglot; // 주차장 여부

        @JacksonXmlProperty(localName = "mt13s")
        private Mt13s mt13s;
    }

    @Getter
    @NoArgsConstructor
    public static class Mt13s {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "mt13")
        private List<Mt13> mt13List;
    }

    @Getter
    @NoArgsConstructor
    public static class Mt13 {
        @JacksonXmlProperty(localName = "prfplcnm")
        private String prfplcnm; // 공연장명

        @JacksonXmlProperty(localName = "mt13id")
        private String mt13id; // 공연장 ID

        @JacksonXmlProperty(localName = "seatscale")
        private String seatscale; // 좌석수
    }

    public static ConcertHall toEntity(final KopisHallDetailResponse response, final int idx) {
        Db db = response.getDb();
        String hallName = getHallName(db, idx);

        return ConcertHall.builder()
                .id(db.mt13s.mt13List.get(idx).mt13id)
                .name(hallName)
                .seatScale(Integer.parseInt(db.mt13s.mt13List.get(idx).seatscale.replace(",", "")))
                .convenienceInfo(ConvenienceInfo.builder()
                        .hasStore(toBoolean(db.store))
                        .hasCafe(toBoolean(db.cafe))
                        .hasParkingLot(toBoolean(db.parkinglot))
                        .hasRestaurant(toBoolean(db.restaurant))
                        .hasDisabledParking(toBoolean(db.parkbarrier))
                        .hasElevator(toBoolean(db.elevbarrier))
                        .hasDisabledToilet(toBoolean(db.restbarrier))
                        .hasRunway(toBoolean(db.runwbarrier))
                        .build())
                .location(Location.builder()
                        .longitude(Double.parseDouble(db.lo))
                        .latitude(Double.parseDouble(db.la))
                        .address(db.adres)
                        .build())
                .build();
    }

    private static String getHallName(final Db db, final int idx) {
        String fcltyName = db.fcltynm;
        String prfplcName = db.mt13s.mt13List.get(idx).prfplcnm;
        if (fcltyName.equals(prfplcName)) {
            return fcltyName;
        }
        return db.fcltynm + " " + db.mt13s.mt13List.get(idx).prfplcnm;
    }

    private static boolean toBoolean(String YN) {
        return "Y".equals(YN);
    }
}
