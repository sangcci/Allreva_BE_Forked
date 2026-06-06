package com.backend.allreva.search.domain;

import lombok.Builder;

@Builder
public record PopularKeywordRankItem(int rank, String keyword, PopularKeywordChangeStatus changeStatus) {}
