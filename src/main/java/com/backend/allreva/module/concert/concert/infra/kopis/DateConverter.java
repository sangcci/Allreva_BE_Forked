package com.backend.allreva.module.concert.concert.infra.kopis;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateConverter {
    public static LocalDate convertToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return LocalDate.parse(date, formatter);
    }

    public static String convertToyyyyMMdd(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return date.format(formatter);
    }
}
