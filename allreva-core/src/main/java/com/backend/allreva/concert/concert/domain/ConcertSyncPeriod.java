package com.backend.allreva.concert.concert.domain;

import java.time.LocalDate;
import java.time.YearMonth;

public record ConcertSyncPeriod(LocalDate today, LocalDate startDate, LocalDate endDate) {

    public static ConcertSyncPeriod from(final LocalDate today) {
        YearMonth yearMonth = YearMonth.from(today);
        return new ConcertSyncPeriod(today, yearMonth.atDay(1), yearMonth.atEndOfMonth());
    }
}
