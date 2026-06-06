package com.backend.allreva.recruitment.rent.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Route {
    private String boardingArea;
    private String dropOffArea;
    private String time;
}
