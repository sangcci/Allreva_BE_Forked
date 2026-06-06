package com.backend.allreva.recruitment.rent;

import com.backend.allreva.recruitment.rent.command.input.RentJoinIdCommand;
import jakarta.validation.constraints.NotNull;

public record RentJoinIdRequest(@NotNull Long rentParticipantId) {

    public RentJoinIdCommand toCommand() {
        return new RentJoinIdCommand(rentParticipantId);
    }
}
