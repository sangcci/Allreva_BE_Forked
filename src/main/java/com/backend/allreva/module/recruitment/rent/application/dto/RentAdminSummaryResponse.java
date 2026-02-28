package com.backend.allreva.module.recruitment.rent.application.dto;

import com.backend.allreva.module.recruitment.rent.domain.Rent;
import com.backend.allreva.module.recruitment.rent.domain.RentBoardingInfo;
import com.backend.allreva.module.recruitment.rent.domain.value.BusSize;
import com.backend.allreva.module.recruitment.rent.domain.value.BusType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record RentAdminSummaryResponse(
        Long rentId,
        String title,
        LocalDate boardingDate,
        String boardingArea,
        LocalDateTime rentStartDate,
        LocalDate rentEndDate,
        int recruitmentCount,
        int participationCount,
        boolean isClosed,
        BusSize busSize,
        BusType busType,
        int maxPassenger) {
    public static RentAdminSummaryResponse from(final Rent rent, final RentBoardingInfo rentBoardingInfo) {
        return new RentAdminSummaryResponse(
                rent.getId(),
                rent.getTitle(),
                rentBoardingInfo.getDate(),
                rent.getBoardingArea(),
                rent.getCreatedAt(),
                rent.getEndDate(),
                rentBoardingInfo.getRecruitmentCount(),
                rentBoardingInfo.getPassengerCount(),
                rent.isClosed(),
                rent.getBus().getBusSize(),
                rent.getBus().getBusType(),
                rent.getBus().getMaxPassenger());
    }
}
