package com.backend.allreva.module.recruitment.rent.application.command.dto;

import jakarta.validation.constraints.NotNull;

public record RentIdRequest(@NotNull Long rentId) {}
