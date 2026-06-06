package com.backend.allreva.recruitment.rent.command.event;

import com.backend.allreva.common.event.Event;
import com.backend.allreva.recruitment.rent.domain.Rent;
import com.backend.allreva.recruitment.rent.domain.RentParticipant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RentJoinedEvent extends Event {

    private final Long rentId;
    private final Long hostMemberId;
    private final Long participantMemberId;
    private final Long rentParticipantId;
    private final String title;

    public static RentJoinedEvent from(final Rent rent, final RentParticipant participant) {
        return RentJoinedEvent.builder()
                .rentId(rent.getId())
                .hostMemberId(rent.getMemberId())
                .participantMemberId(participant.getMemberId())
                .rentParticipantId(participant.getId())
                .title(rent.getTitle())
                .build();
    }
}
