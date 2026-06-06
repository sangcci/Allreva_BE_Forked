package com.backend.allreva.concert.concert.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = {"name", "salesUrl"})
public class Seller {

    private String name;
    private String salesUrl;

    @Builder
    private Seller(final String name, final String salesUrl) {
        this.name = name;
        this.salesUrl = salesUrl;
    }
}
