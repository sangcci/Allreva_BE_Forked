package com.backend.allreva.concert.concert.query.model;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.concert.concert.domain.Concert;
import com.backend.allreva.concert.concert.domain.ConcertInfo;
import com.backend.allreva.concert.concert.domain.Seller;
import com.backend.allreva.concert.place.domain.ConcertHall;
import com.backend.allreva.concert.place.domain.ConvenienceInfo;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record ConcertDetailResult(
        Image poster,
        List<Image> detailImages,
        ConcertInfo concertInfo,
        Set<Seller> sellers,
        String hallCode,
        String hallName,
        Integer seatScale,
        ConvenienceInfo convenienceInfo,
        String address) {

    public static final ConcertDetailResult EMPTY =
            ConcertDetailResult.builder().build();

    public static ConcertDetailResult from(final Concert concert, final ConcertHall hall) {
        ConcertDetailResult.ConcertDetailResultBuilder builder = ConcertDetailResult.builder()
                .poster(concert.getPoster())
                .detailImages(concert.getDetailImages())
                .concertInfo(concert.getConcertInfo())
                .sellers(concert.getSellers());
        if (hall != null) {
            builder.hallCode(hall.getHallCode())
                    .hallName(hall.getName())
                    .seatScale(hall.getSeatScale())
                    .convenienceInfo(hall.getConvenienceInfo())
                    .address(hall.getLocation() != null ? hall.getLocation().getAddress() : null);
        }
        return builder.build();
    }

    public static ConcertDetailResult from(final ConcertDetail concert) {
        return ConcertDetailResult.builder()
                .poster(concert.poster())
                .detailImages(concert.detailImages())
                .concertInfo(concert.concertInfo())
                .sellers(concert.sellers())
                .hallCode(concert.hallCode())
                .hallName(concert.hallName())
                .seatScale(concert.seatScale())
                .convenienceInfo(concert.convenienceInfo())
                .address(concert.address())
                .build();
    }
}
