package com.backend.allreva.search.query.model;

import com.backend.allreva.search.domain.ChangeStatus;
import lombok.Builder;

@Builder
public record PopularKeywordResult(int rank, String keyword, ChangeStatus changeStatus) {

    public static PopularKeywordResult from(final com.backend.allreva.search.domain.PopularKeywordRankItem source) {
        return PopularKeywordResult.builder()
                .rank(source.rank())
                .keyword(source.keyword())
                .changeStatus(ChangeStatus.valueOf(source.changeStatus().name()))
                .build();
    }
}
