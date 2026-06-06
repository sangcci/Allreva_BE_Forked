package com.backend.allreva.recruitment.rent.command.input;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.recruitment.rent.domain.BoardingType;
import com.backend.allreva.recruitment.rent.domain.Bus;
import com.backend.allreva.recruitment.rent.domain.BusSize;
import com.backend.allreva.recruitment.rent.domain.BusType;
import com.backend.allreva.recruitment.rent.domain.Rent;
import com.backend.allreva.recruitment.rent.domain.Route;
import java.time.LocalDate;
import java.util.List;

public record RentRegisterCommand(
        String concertCode,
        String title,
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
        Image image) {
    public Rent toEntity(final Long memberId) {
        return Rent.builder()
                .memberId(memberId)
                .concertCode(concertCode)
                .title(title)
                .image(image)
                .region(region)
                .boardingType(boardingType)
                .upRoute(upRoute)
                .downRoute(downRoute)
                .bus(Bus.builder()
                        .busSize(busSize)
                        .busType(busType)
                        .maxPassenger(maxPassenger)
                        .build())
                .price(price)
                .endDate(endDate)
                .information(information)
                .build();
    }
}
