package com.backend.allreva.recruitment.rent.command.implementation;

import com.backend.allreva.recruitment.rent.command.input.RentJoinCommand;
import com.backend.allreva.recruitment.rent.domain.Rent;
import com.backend.allreva.recruitment.rent.domain.RentParticipant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RentJoiner {

    private final RentParticipantReader rentParticipantReader;
    private final RentSeatReserver rentSeatReserver;

    public RentParticipant join(final Rent rent, final RentJoinCommand command, final Long memberId) {
        rent.validateHostCannotJoin(memberId);
        rentParticipantReader.validateNotAlreadyJoined(memberId, rent.getId(), command.boardingDate());
        rentSeatReserver.reserve(command);
        return command.toParticipant(memberId);
    }
}
