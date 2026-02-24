package com.backend.allreva.module.recruitment.rent.application.dto;

import jakarta.validation.constraints.NotNull;

public record RentJoinIdRequest(
        @NotNull
        Long rentParticipantId
) {

}
