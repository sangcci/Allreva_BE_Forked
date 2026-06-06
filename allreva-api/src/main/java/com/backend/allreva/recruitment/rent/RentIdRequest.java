package com.backend.allreva.recruitment.rent;

import com.backend.allreva.recruitment.rent.command.input.RentIdCommand;
import jakarta.validation.constraints.NotNull;

public record RentIdRequest(@NotNull Long rentId) {

    public RentIdCommand toCommand() {
        return new RentIdCommand(rentId);
    }
}
