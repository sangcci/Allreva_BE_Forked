package com.backend.allreva.module.concert.place.application.dto;

import java.time.LocalDate;

public record RelatedConcertResponse(
        Long concertId, String title, LocalDate startDate, LocalDate endDate, String posterUrl) {}
