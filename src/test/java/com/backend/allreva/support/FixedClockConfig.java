package com.backend.allreva.support;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class FixedClockConfig {

    @Bean
    public Clock clock() {
        return Clock.fixed(
                LocalDate.of(2024, 9, 1).atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
    }
}
