package com.backend.allreva.module.search.application.dto;

import java.time.LocalDate;

public record RentThumbnail(Long id, String title, String region, String imageUrl, LocalDate endDate) {}
