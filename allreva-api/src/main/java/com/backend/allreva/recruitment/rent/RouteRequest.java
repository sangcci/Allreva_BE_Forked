package com.backend.allreva.recruitment.rent;

import com.backend.allreva.recruitment.rent.domain.Route;
import jakarta.validation.constraints.NotBlank;

public record RouteRequest(
        @NotBlank String boardingArea,
        @NotBlank String dropOffArea,
        @NotBlank String time) {

    public Route toDomain() {
        return Route.builder()
                .boardingArea(boardingArea)
                .dropOffArea(dropOffArea)
                .time(time)
                .build();
    }
}
