package com.backend.allreva.module.recruitment.rent.application.dto;

import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.recruitment.rent.domain.Rent;
import com.backend.allreva.module.recruitment.rent.domain.value.BusSize;
import com.backend.allreva.module.recruitment.rent.domain.value.BusType;
import com.backend.allreva.module.recruitment.rent.domain.value.RefundType;
import com.backend.allreva.module.recruitment.rent.domain.value.Region;
import java.time.LocalDate;
import java.util.List;

public record RentDetailResponse(
        String concertName,
        String imageUrl,
        String title,
        String artistName,
        Region region,
        String boardingArea,
        String dropOffArea,
        String upTime,
        String downTime,
        List<RentBoardingDateResponse> boardingDates,
        BusSize busSize,
        BusType busType,
        int maxPassenger,
        int roundPrice,
        int upTimePrice,
        int downTimePrice,
        int recruitmentCount,
        LocalDate endDate,
        String chatUrl,
        RefundType refundType,
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
                rent.getArtistName(),
                rent.getRegion(),
                rent.getBoardingArea(),
                concertHall != null ? concertHall.getName() : null,
                rent.getUpTime(),
                rent.getDownTime(),
                boardingDates,
                rent.getBus().getBusSize(),
                rent.getBus().getBusType(),
                rent.getBus().getMaxPassenger(),
                rent.getPrice().getRoundPrice(),
                rent.getPrice().getUpTimePrice(),
                rent.getPrice().getDownTimePrice(),
                rent.getBoardingSlots().isEmpty()
                        ? 0
                        : rent.getBoardingSlots().get(0).getRecruitmentCount(),
                rent.getEndDate(),
                rent.getChatUrl(),
                rent.getRefundType(),
                rent.getInformation(),
                rent.isClosed());
    }

    public record RentBoardingDateResponse(LocalDate date, int participationCount) {}
}
