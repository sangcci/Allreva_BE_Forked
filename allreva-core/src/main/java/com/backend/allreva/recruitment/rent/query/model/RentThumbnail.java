package com.backend.allreva.recruitment.rent.query.model;

import java.time.LocalDate;

public record RentThumbnail(Long id, String title, String region, String imageUrl, LocalDate endDate) {}
