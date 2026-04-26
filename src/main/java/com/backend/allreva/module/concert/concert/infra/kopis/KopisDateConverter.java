package com.backend.allreva.module.concert.concert.infra.kopis;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class KopisDateConverter {

    private static final DateTimeFormatter KOPIS_RESPONSE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter KOPIS_REQUEST_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static LocalDate toLocalDate(String date) {
        return LocalDate.parse(date, KOPIS_RESPONSE_FORMAT);
    }

    public static String toKopisFormat(LocalDate date) {
        return date.format(KOPIS_REQUEST_FORMAT);
    }
}
