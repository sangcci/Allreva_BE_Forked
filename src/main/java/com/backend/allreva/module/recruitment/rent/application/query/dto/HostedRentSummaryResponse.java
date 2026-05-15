package com.backend.allreva.module.recruitment.rent.application.query.dto;

import com.backend.allreva.module.recruitment.rent.domain.Rent;
import com.backend.allreva.module.recruitment.rent.domain.RentBoardingSlot;
import com.backend.allreva.module.recruitment.rent.domain.value.BoardingType;
import com.backend.allreva.module.recruitment.rent.domain.value.BusSize;
import com.backend.allreva.module.recruitment.rent.domain.value.BusType;
import com.backend.allreva.module.recruitment.rent.domain.value.Route;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record HostedRentSummaryResponse(
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

    public static HostedRentSummaryResponse from(final Rent rent) {
        List<BoardingSlotSummary> slots =
                rent.getBoardingSlots().stream().map(BoardingSlotSummary::from).toList();
        return new HostedRentSummaryResponse(
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

    public record BoardingSlotSummary(LocalDate boardingDate, int recruitmentCount, int passengerCount) {
        public static BoardingSlotSummary from(final RentBoardingSlot slot) {
            return new BoardingSlotSummary(slot.getDate(), slot.getRecruitmentCount(), slot.getPassengerCount());
        }
    }
}
