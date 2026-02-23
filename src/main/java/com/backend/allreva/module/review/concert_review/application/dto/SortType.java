package com.backend.allreva.module.review.concert_review.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortType {
    CREATED_ASC("오래된순"),
    CREATED_DESC("최신순");

    private final String description;
}
