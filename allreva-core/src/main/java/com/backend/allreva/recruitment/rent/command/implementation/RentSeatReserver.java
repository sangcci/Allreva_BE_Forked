package com.backend.allreva.recruitment.rent.command.implementation;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.recruitment.rent.command.input.RentJoinCommand;
import com.backend.allreva.recruitment.rent.domain.RentBoardingSlotRepository;
import com.backend.allreva.recruitment.rent.domain.RentErrorCode;
import com.backend.allreva.recruitment.rent.domain.RentParticipant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RentSeatReserver {

    private final RentBoardingSlotRepository rentBoardingSlotRepository;

    @Transactional
    public void reserve(final RentJoinCommand command) {
        int updatedRowCount = rentBoardingSlotRepository.incrementPassengerCount(
                command.rentId(), command.boardingDate(), command.passengerNum());
        if (updatedRowCount == 0) {
            throw new CustomException(RentErrorCode.SLOT_FULL);
        }
    }

    @Transactional
    public void release(final RentParticipant participant) {
        int updated = rentBoardingSlotRepository.decrementPassengerCount(
                participant.getRentId(), participant.getBoardingDate(), participant.getPassengerNum());
        if (updated == 0) {
            log.warn(
                    "passengerCount decrement skipped: rentId={}, boardingDate={}, passengerNum={}",
                    participant.getRentId(),
                    participant.getBoardingDate(),
                    participant.getPassengerNum());
        }
    }
}
