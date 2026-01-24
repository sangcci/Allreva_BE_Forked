package com.backend.allreva.module.concert.review.application.dto;

public record FileData(
        byte[] bytes,
        String filename
) {
}
