package com.backend.allreva.module.concert.place.application.dto;

import com.backend.allreva.module.concert.place.domain.value.ConvenienceInfo;
import com.backend.allreva.module.concert.place.domain.value.Location;

public record ConcertHallDetailResponse(
        String name, Integer seatScale, Double star, ConvenienceInfo convenienceInfo, Location location) {}
