package com.backend.allreva.recruitment.rent.query.model;

import com.backend.allreva.concert.concert.domain.Concert;
import com.backend.allreva.concert.place.domain.ConcertHall;
import com.backend.allreva.recruitment.rent.domain.BoardingType;
import com.backend.allreva.recruitment.rent.domain.BusSize;
import com.backend.allreva.recruitment.rent.domain.BusType;
import com.backend.allreva.recruitment.rent.domain.Rent;
import com.backend.allreva.recruitment.rent.domain.Route;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public record RentDetailResult(
        String concertName,
        String imageUrl,
        String title,
        List<String> castNames,
        String region,
        BoardingType boardingType,
        Route upRoute,
        Route downRoute,
        List<RentBoardingDateResult> boardingDates,
        BusSize busSize,
        BusType busType,
        int maxPassenger,
        int price,
        int recruitmentCount,
        LocalDate endDate,
        String information,
        boolean isClosed) {

    public static RentDetailResult from(final Rent rent, final Concert concert, final ConcertHall concertHall) {
        List<RentBoardingDateResult> boardingDates = rent.getBoardingSlots().stream()
                .map(slot -> new RentBoardingDateResult(slot.getDate(), slot.getPassengerCount()))
                .toList();

        return new RentDetailResult(
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

    public static RentDetailResult from(final RentDetail rent) {
        return new RentDetailResult(
                rent.concertName(),
                rent.imageUrl(),
                rent.title(),
                rent.castNames(),
                rent.region(),
                rent.boardingType(),
                rent.upRoute(),
                rent.downRoute(),
                rent.boardingDates().stream()
                        .map(date -> new RentBoardingDateResult(date.date(), date.participationCount()))
                        .toList(),
                rent.busSize(),
                rent.busType(),
                rent.maxPassenger(),
                rent.price(),
                rent.recruitmentCount(),
                rent.endDate(),
                rent.information(),
                rent.isClosed());
    }

    public record RentBoardingDateResult(LocalDate date, int participationCount) {}
}
