package com.backend.allreva.concert.concert;

import com.backend.allreva.common.converter.AbstractJsonSetConverter;
import jakarta.persistence.Converter;

@Converter
public class SellerVOSetConverter extends AbstractJsonSetConverter<SellerVO> {

    public SellerVOSetConverter() {
        super(SellerVO.class);
    }
}
