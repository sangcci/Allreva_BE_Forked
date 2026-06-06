package com.backend.allreva.recruitment.rent.command.implementation;

import com.backend.allreva.recruitment.rent.domain.RentParticipant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RentParticipantCanceller {

    private final RentSeatReserver rentSeatReserver;

    public void cancel(final RentParticipant participant, final Long memberId) {
        participant.validateMine(memberId);
        rentSeatReserver.release(participant);
    }
}
