package com.backend.allreva.module.concert.concert.infra.jpa;

import com.backend.allreva.module.concert.concert.domain.value.Seller;
import com.backend.allreva.util.converter.AbstractJsonSetConverter;
import jakarta.persistence.Converter;

@Converter
public class SellerSetConverter extends AbstractJsonSetConverter<Seller> {

    public SellerSetConverter() {
        super(Seller.class);
    }
}
