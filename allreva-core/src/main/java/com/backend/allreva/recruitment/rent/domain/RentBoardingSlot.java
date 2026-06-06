package com.backend.allreva.recruitment.rent.domain;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RentBoardingSlot {

    private Long id;
    private LocalDate date;
    private int recruitmentCount;
    private int passengerCount;

    public static RentBoardingSlot open(final LocalDate date, final int recruitmentCount) {
        return RentBoardingSlot.builder()
                .date(date)
                .recruitmentCount(recruitmentCount)
                .passengerCount(0)
                .build();
    }
}
