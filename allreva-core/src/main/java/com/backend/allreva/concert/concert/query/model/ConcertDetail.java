package com.backend.allreva.concert.concert.query.model;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.concert.concert.domain.ConcertInfo;
import com.backend.allreva.concert.concert.domain.Seller;
import com.backend.allreva.concert.place.domain.ConvenienceInfo;
import java.util.List;
import java.util.Set;

public record ConcertDetail(
        Image poster,
        List<Image> detailImages,
        ConcertInfo concertInfo,
        Set<Seller> sellers,
        String hallCode,
        String hallName,
        Integer seatScale,
        ConvenienceInfo convenienceInfo,
        String address) {}
