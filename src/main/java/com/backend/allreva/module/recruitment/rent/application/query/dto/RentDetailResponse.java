package com.backend.allreva.module.recruitment.rent.application.query.dto;

import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.recruitment.rent.domain.Rent;
import com.backend.allreva.module.recruitment.rent.domain.value.BoardingType;
import com.backend.allreva.module.recruitment.rent.domain.value.BusSize;
import com.backend.allreva.module.recruitment.rent.domain.value.BusType;
import com.backend.allreva.module.recruitment.rent.domain.value.Route;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public record RentDetailResponse(
        String concertName,
        String imageUrl,
        String title,
        List<String> castNames,
        String region,
        BoardingType boardingType,
        Route upRoute,
        Route downRoute,
        List<RentBoardingDateResponse> boardingDates,
        BusSize busSize,
        BusType busType,
        int maxPassenger,
        int price,
        int recruitmentCount,
        LocalDate endDate,
        String information,
        boolean isClosed) {

    public static RentDetailResponse from(final Rent rent, final Concert concert, final ConcertHall concertHall) {
        List<RentBoardingDateResponse> boardingDates = rent.getBoardingSlots().stream()
                .map(slot -> new RentBoardingDateResponse(slot.getDate(), slot.getPassengerCount()))
                .toList();

        return new RentDetailResponse(
                concert != null ? concert.getConcertInfo().getTitle() : null,
                rent.getImage().getUrl(),
                rent.getTitle(),
                concert != null ? concert.getCastNames() : Collections.emptyList(),
                rent.getRegion(),
                rent.getBoardingType(),
                rent.getUpRoute(),
                rent.getDownRoute(),
                boardingDates,
                rent.getBus().getBusSize(),
                rent.getBus().getBusType(),
                rent.getBus().getMaxPassenger(),
                rent.getPrice(),
                rent.getBoardingSlots().isEmpty()
                        ? 0
                        : rent.getBoardingSlots().get(0).getRecruitmentCount(),
                rent.getEndDate(),
                rent.getInformation(),
                rent.isClosed());
    }

    public record RentBoardingDateResponse(LocalDate date, int participationCount) {}
}
