package com.backend.allreva.module.recruitment.rent.domain.value;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Route {

    @NotBlank
    private String boardingArea;

    @NotBlank
    private String dropOffArea;

    @NotBlank
    private String time;
}
