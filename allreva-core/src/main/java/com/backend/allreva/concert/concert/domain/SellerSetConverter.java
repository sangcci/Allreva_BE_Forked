package com.backend.allreva.concert.concert.domain;

import com.backend.allreva.common.converter.AbstractJsonSetConverter;
import jakarta.persistence.Converter;

@Converter
public class SellerSetConverter extends AbstractJsonSetConverter<Seller> {

    public SellerSetConverter() {
        super(Seller.class);
    }
}
