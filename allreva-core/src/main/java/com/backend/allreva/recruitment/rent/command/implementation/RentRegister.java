package com.backend.allreva.recruitment.rent.command.implementation;

import com.backend.allreva.recruitment.rent.command.input.RentRegisterCommand;
import com.backend.allreva.recruitment.rent.domain.Rent;
import com.backend.allreva.recruitment.rent.domain.RentBoardingSlot;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RentRegister {

    public Rent register(final RentRegisterCommand command, final Long memberId) {
        Rent rent = command.toEntity(memberId);
        rent.openBoardingSlots(createBoardingSlots(command.rentBoardingDateRequests(), command.recruitmentCount()));
        return rent;
    }

    private List<RentBoardingSlot> createBoardingSlots(
            final List<LocalDate> boardingDates, final int recruitmentCount) {
        return boardingDates.stream()
                .map(date -> RentBoardingSlot.open(date, recruitmentCount))
                .toList();
    }
}
