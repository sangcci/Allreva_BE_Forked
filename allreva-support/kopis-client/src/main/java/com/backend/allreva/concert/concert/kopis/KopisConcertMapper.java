package com.backend.allreva.concert.concert.kopis;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.concert.concert.domain.Concert;
import com.backend.allreva.concert.concert.domain.ConcertInfo;
import com.backend.allreva.concert.concert.domain.ConcertStatus;
import com.backend.allreva.concert.concert.domain.DateInfo;
import com.backend.allreva.concert.concert.domain.Seller;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class KopisConcertMapper {

    public Concert toConcert(final String hallCode, final KopisConcertDetailResponse response) {
        KopisConcertDetailResponse.Db db = response.getDb();
        return Concert.builder()
                .concertCode(db.getConcertCode())
                .hallCode(hallCode)
                .concertInfo(ConcertInfo.builder()
                        .title(db.getPrfnm())
                        .host(db.getEntrpsnmH())
                        .price(db.getPcseguidance())
                        .performStatus(ConcertStatus.convertToConcertStatus(db.getPrfstate()))
                        .dateInfo(DateInfo.builder()
                                .startDate(KopisDateConverter.toLocalDate(db.getPrfpdfrom()))
                                .endDate(KopisDateConverter.toLocalDate(db.getPrfpdto()))
                                .timeTable(db.getTimetable())
                                .build())
                        .build())
                .poster(new Image(db.getPoster()))
                .detailImages(toImages(db.getStyurls()))
                .sellers(toSellers(db.getRelates()))
                .castNames(KopisCastnameParser.parseCastNames(db.getPrfcast()))
                .build();
    }

    private List<Image> toImages(final List<String> styurls) {
        if (styurls == null) {
            return List.of();
        }
        return styurls.stream().map(Image::new).toList();
    }

    private Set<Seller> toSellers(final Set<KopisConcertDetailResponse.Db.Relate> relates) {
        if (relates == null) {
            return Set.of();
        }
        return relates.stream()
                .map(relate -> Seller.builder()
                        .name(relate.getRelatenm())
                        .salesUrl(relate.getRelateurl())
                        .build())
                .collect(Collectors.toSet());
    }
}
