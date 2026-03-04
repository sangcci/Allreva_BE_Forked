package com.backend.allreva.module.concert.concert.domain.value;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class DateInfo {

    @Column(nullable = false, name = "stdate")
    private LocalDate startDate;

    @Column(nullable = false, name = "eddate")
    private LocalDate endDate;

    @Column(nullable = false)
    private String timeTable;

    @Builder
    private DateInfo(LocalDate startDate, LocalDate endDate, String timeTable) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.timeTable = timeTable;
    }
}
