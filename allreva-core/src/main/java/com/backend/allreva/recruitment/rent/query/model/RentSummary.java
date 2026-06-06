package com.backend.allreva.recruitment.rent.query.model;

import com.backend.allreva.recruitment.rent.domain.Route;
import java.time.LocalDate;

public record RentSummary(
        Long rentId, String title, String region, Route upRoute, Route downRoute, LocalDate endDate, String imageUrl) {}
