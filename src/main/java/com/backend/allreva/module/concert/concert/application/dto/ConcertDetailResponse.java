package com.backend.allreva.module.concert.concert.application.dto;


import com.backend.allreva.common.model.Image;
import com.backend.allreva.module.concert.concert.domain.value.ConcertInfo;
import com.backend.allreva.module.concert.concert.domain.value.Seller;
import com.backend.allreva.module.concert.place.domain.value.ConvenienceInfo;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;
import java.util.Set;

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
        String address

) {
    public static final ConcertDetailResponse EMPTY = ConcertDetailResponse.builder().build();
}
