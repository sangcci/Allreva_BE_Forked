package com.backend.allreva.concert.concert.query.model;

import java.time.LocalDate;

public record RelatedConcert(
        String concertCode, String title, LocalDate startDate, LocalDate endDate, String posterUrl) {}
