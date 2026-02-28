package com.backend.allreva.module.concert.concert.application.dto;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.value.ConcertInfo;
import com.backend.allreva.module.concert.concert.domain.value.Seller;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.value.ConvenienceInfo;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record ConcertDetailResponse(
        Image poster,
        List<Image> detailImages,
        ConcertInfo concertInfo,
        Set<Seller> sellers,
        String hallCode,
        String hallName,
        Integer seatScale,
        ConvenienceInfo convenienceInfo,
        String address) {

    public static final ConcertDetailResponse EMPTY =
            ConcertDetailResponse.builder().build();

    public static ConcertDetailResponse from(final Concert concert, final ConcertHall hall) {
        ConcertDetailResponse.ConcertDetailResponseBuilder builder = ConcertDetailResponse.builder()
                .poster(concert.getPoster())
                .detailImages(concert.getDetailImages())
                .concertInfo(concert.getConcertInfo())
                .sellers(concert.getSellers());
        if (hall != null) {
            builder.hallCode(hall.getId())
                    .hallName(hall.getName())
                    .seatScale(hall.getSeatScale())
                    .convenienceInfo(hall.getConvenienceInfo())
                    .address(hall.getLocation() != null ? hall.getLocation().getAddress() : null);
        }
        return builder.build();
    }
}
