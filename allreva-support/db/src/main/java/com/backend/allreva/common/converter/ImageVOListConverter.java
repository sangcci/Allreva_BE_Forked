package com.backend.allreva.common.converter;

import com.backend.allreva.common.persistence.ImageVO;
import jakarta.persistence.Converter;

@Converter
public class ImageVOListConverter extends AbstractJsonListConverter<ImageVO> {

    public ImageVOListConverter() {
        super(ImageVO.class);
    }
}
