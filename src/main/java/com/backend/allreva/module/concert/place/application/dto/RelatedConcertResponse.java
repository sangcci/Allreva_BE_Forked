package com.backend.allreva.module.concert.place.application.dto;

import java.time.LocalDate;

public record RelatedConcertResponse(
        String concertCode, String title, LocalDate startDate, LocalDate endDate, String posterUrl) {}
