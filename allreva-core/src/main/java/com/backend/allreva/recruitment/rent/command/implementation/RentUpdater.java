package com.backend.allreva.recruitment.rent.command.implementation;

import com.backend.allreva.recruitment.rent.command.input.RentUpdateCommand;
import com.backend.allreva.recruitment.rent.domain.Bus;
import com.backend.allreva.recruitment.rent.domain.Rent;
import com.backend.allreva.recruitment.rent.domain.RentBoardingSlot;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RentUpdater {

    public void update(final Rent rent, final RentUpdateCommand command) {
        rent.updateDetails(
                command.image(),
                command.region(),
                command.boardingType(),
                command.upRoute(),
                command.downRoute(),
                Bus.builder()
                        .busSize(command.busSize())
                        .busType(command.busType())
                        .maxPassenger(command.maxPassenger())
                        .build(),
                command.price(),
                command.endDate(),
                command.information());
        rent.replaceBoardingSlots(createBoardingSlots(command.rentBoardingDateRequests(), command.recruitmentCount()));
    }

    private List<RentBoardingSlot> createBoardingSlots(
            final List<LocalDate> boardingDates, final int recruitmentCount) {
        return boardingDates.stream()
                .map(date -> RentBoardingSlot.open(date, recruitmentCount))
                .toList();
    }
}
