package com.backend.allreva.module.concert.concert.infra.kopis;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.value.ConcertInfo;
import com.backend.allreva.module.concert.concert.domain.value.ConcertStatus;
import com.backend.allreva.module.concert.concert.domain.value.DateInfo;
import com.backend.allreva.module.concert.concert.domain.value.Seller;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JacksonXmlRootElement(localName = "dbs")
public class KopisConcertDetailResponse {

    @JacksonXmlProperty(localName = "db")
    private Db db;

    @Getter
    @NoArgsConstructor
    public static class Db {
        @JacksonXmlProperty(localName = "mt20id")
        private String concertCode; // 공연 code

        @JacksonXmlProperty(localName = "prfnm")
        private String prfnm; // 공연명

        @JacksonXmlProperty(localName = "prfpdfrom")
        private String prfpdfrom; // 시작 날짜

        @JacksonXmlProperty(localName = "prfpdto")
        private String prfpdto; // 종료 날짜

        @JacksonXmlProperty(localName = "prfcast")
        private String prfcast; // 공연 출연진

        @JacksonXmlProperty(localName = "poster")
        private String poster; // 포스터

        @JacksonXmlProperty(localName = "pcseguidance")
        private String pcseguidance; // 가격

        @JacksonXmlProperty(localName = "prfstate")
        private String prfstate; // 공연상태

        @JacksonXmlProperty(localName = "dtguidance")
        private String timetable; // 공연 타임테이블

        @JacksonXmlProperty(localName = "entrpsnmH")
        private String entrpsnmH; // 주최

        @JacksonXmlElementWrapper(localName = "styurls")
        @JacksonXmlProperty(localName = "styurl")
        private List<String> styurls; // 소개이미지 list

        @JacksonXmlElementWrapper(localName = "relates")
        @JacksonXmlProperty(localName = "relate")
        private Set<Relate> relates; // 판매처 list

        @Getter
        @NoArgsConstructor
        public static class Relate {
            @JacksonXmlProperty(localName = "relatenm")
            private String relatenm;

            @JacksonXmlProperty(localName = "relateurl")
            private String relateurl;
        }
    }

    public static Concert toEntity(final String hallCode, final KopisConcertDetailResponse response) {
        Db db = response.getDb();
        return Concert.builder()
                .concertCode(db.concertCode)
                .hallCode(hallCode)
                .concertInfo(ConcertInfo.builder()
                        .title(db.prfnm)
                        .host(db.entrpsnmH)
                        .price(db.pcseguidance)
                        .performStatus(ConcertStatus.convertToConcertStatus(db.prfstate))
                        .dateInfo(DateInfo.builder()
                                .startDate(KopisDateConverter.toLocalDate(db.prfpdfrom))
                                .endDate(KopisDateConverter.toLocalDate(db.prfpdto))
                                .timeTable(db.timetable)
                                .build())
                        .build())
                .poster(new Image(db.poster))
                .detailImages(convertToImage(db.styurls))
                .sellers(convertToSellers(db.relates))
                .build();
    }

    public static List<Image> convertToImage(final List<String> styurls) {
        return styurls.stream().map(image -> new Image(image)).toList();
    }

    public static Set<Seller> convertToSellers(final Set<Db.Relate> relates) {
        if (relates == null) return Set.of();
        return relates.stream()
                .map(relate -> Seller.builder()
                        .name(relate.getRelatenm())
                        .salesUrl(relate.getRelateurl())
                        .build())
                .collect(Collectors.toSet());
    }
}
