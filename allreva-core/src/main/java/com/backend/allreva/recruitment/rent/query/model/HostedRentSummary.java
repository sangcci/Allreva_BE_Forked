package com.backend.allreva.recruitment.rent.query.model;

import com.backend.allreva.recruitment.rent.domain.BoardingType;
import com.backend.allreva.recruitment.rent.domain.BusSize;
import com.backend.allreva.recruitment.rent.domain.BusType;
import com.backend.allreva.recruitment.rent.domain.Route;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record HostedRentSummary(
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

    public record BoardingSlotSummary(LocalDate boardingDate, int recruitmentCount, int passengerCount) {}
}
