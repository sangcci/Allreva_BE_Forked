package com.backend.allreva.recruitment.rent.query.model;

import com.backend.allreva.recruitment.rent.domain.BoardingType;
import com.backend.allreva.recruitment.rent.domain.BusSize;
import com.backend.allreva.recruitment.rent.domain.BusType;
import com.backend.allreva.recruitment.rent.domain.Rent;
import com.backend.allreva.recruitment.rent.domain.RentBoardingSlot;
import com.backend.allreva.recruitment.rent.domain.Route;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record HostedRentSummaryResult(
        Long rentId,
        String title,
        BoardingType boardingType,
        Route upRoute,
        Route downRoute,
        LocalDateTime rentStartDate,
        LocalDate rentEndDate,
        boolean isClosed,
        BusSize busSize,
        BusType busType,
        int maxPassenger,
        List<BoardingSlotSummary> boardingSlots) {

    public static HostedRentSummaryResult from(final Rent rent) {
        List<BoardingSlotSummary> slots =
                rent.getBoardingSlots().stream().map(BoardingSlotSummary::from).toList();
        return new HostedRentSummaryResult(
                rent.getId(),
                rent.getTitle(),
                rent.getBoardingType(),
                rent.getUpRoute(),
                rent.getDownRoute(),
                rent.getCreatedAt(),
                rent.getEndDate(),
                rent.isClosed(),
                rent.getBus().getBusSize(),
                rent.getBus().getBusType(),
                rent.getBus().getMaxPassenger(),
                slots);
    }

    public static HostedRentSummaryResult from(final HostedRentSummary rent) {
        return new HostedRentSummaryResult(
                rent.rentId(),
                rent.title(),
                rent.boardingType(),
                rent.upRoute(),
                rent.downRoute(),
                rent.rentStartDate(),
                rent.rentEndDate(),
                rent.isClosed(),
                rent.busSize(),
                rent.busType(),
                rent.maxPassenger(),
                rent.boardingSlots().stream()
                        .map(slot -> new BoardingSlotSummary(
                                slot.boardingDate(), slot.recruitmentCount(), slot.passengerCount()))
                        .toList());
    }

    public record BoardingSlotSummary(LocalDate boardingDate, int recruitmentCount, int passengerCount) {
        public static BoardingSlotSummary from(final RentBoardingSlot slot) {
            return new BoardingSlotSummary(slot.getDate(), slot.getRecruitmentCount(), slot.getPassengerCount());
        }
    }
}
