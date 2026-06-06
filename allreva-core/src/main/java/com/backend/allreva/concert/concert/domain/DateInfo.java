package com.backend.allreva.concert.concert.domain;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DateInfo {

    private LocalDate startDate;
    private LocalDate endDate;
    private String timeTable;

    @Builder
    private DateInfo(LocalDate startDate, LocalDate endDate, String timeTable) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.timeTable = timeTable;
    }
}
