package com.backend.allreva.recruitment.rent.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Bus {

    private BusSize busSize;
    private BusType busType;
    private int maxPassenger;
}
