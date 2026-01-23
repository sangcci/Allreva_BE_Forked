package com.backend.allreva.module.search.application.dto;

import lombok.Builder;

@Builder
public record PopularKeywordResponse(
        int rank,
        String keyword,
        ChangeStatus changeStatus) {

}
