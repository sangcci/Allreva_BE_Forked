package com.backend.allreva.module.concert.concert.application.dto;

import java.time.LocalDate;

public interface ConcertDateInfoResponse {
    LocalDate getStartDate();
    LocalDate getEndDate();
}
