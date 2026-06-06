package com.backend.allreva.concert.concert;

import com.backend.allreva.concert.concert.domain.Seller;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SellerVO {

    private String name;
    private String salesUrl;

    private SellerVO(final String name, final String salesUrl) {
        this.name = name;
        this.salesUrl = salesUrl;
    }

    public static SellerVO from(final Seller seller) {
        return seller == null ? null : new SellerVO(seller.getName(), seller.getSalesUrl());
    }

    public Seller toDomain() {
        return Seller.builder().name(name).salesUrl(salesUrl).build();
    }
}
