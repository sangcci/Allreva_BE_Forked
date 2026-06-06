package com.backend.allreva.recruitment.rent.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Depositor {

    private String depositorName;
    private String depositorTime;
    private String phone;
}
