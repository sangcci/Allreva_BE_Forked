package com.backend.allreva.recruitment.rent.command.event;

import com.backend.allreva.common.event.Event;
import com.backend.allreva.recruitment.rent.domain.Rent;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RentRegisteredEvent extends Event {

    private final Long rentId;
    private final Long hostMemberId;
    private final String title;

    public static RentRegisteredEvent from(final Rent rent) {
        return RentRegisteredEvent.builder()
                .rentId(rent.getId())
                .hostMemberId(rent.getMemberId())
                .title(rent.getTitle())
                .build();
    }
}
