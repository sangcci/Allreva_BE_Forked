package com.backend.allreva.recruitment.rent.query.model;

import com.backend.allreva.recruitment.rent.domain.BoardingType;
import com.backend.allreva.recruitment.rent.domain.BusSize;
import com.backend.allreva.recruitment.rent.domain.BusType;
import com.backend.allreva.recruitment.rent.domain.Route;
import java.time.LocalDate;
import java.util.List;

public record RentDetail(
        String concertName,
        String imageUrl,
        String title,
        List<String> castNames,
        String region,
        BoardingType boardingType,
        Route upRoute,
        Route downRoute,
        List<RentBoardingDate> boardingDates,
        BusSize busSize,
        BusType busType,
        int maxPassenger,
        int price,
        int recruitmentCount,
        LocalDate endDate,
        String information,
        boolean isClosed) {

    public record RentBoardingDate(LocalDate date, int participationCount) {}
}
