package com.backend.allreva.recruitment.rent.command.input;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.recruitment.rent.domain.BoardingType;
import com.backend.allreva.recruitment.rent.domain.BusSize;
import com.backend.allreva.recruitment.rent.domain.BusType;
import com.backend.allreva.recruitment.rent.domain.Route;
import java.time.LocalDate;
import java.util.List;

public record RentUpdateCommand(
        Long rentId,
        String region,
        BoardingType boardingType,
        Route upRoute,
        Route downRoute,

        List<LocalDate> rentBoardingDateRequests,

        BusSize busSize,
        BusType busType,
        int maxPassenger,
        int price,
        int recruitmentCount,
        LocalDate endDate,
        String information,
        Image image) {}
