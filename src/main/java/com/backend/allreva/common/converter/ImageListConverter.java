package com.backend.allreva.common.converter;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.util.converter.AbstractJsonListConverter;
import jakarta.persistence.Converter;

@Converter
public class ImageListConverter extends AbstractJsonListConverter<Image> {

    public ImageListConverter() {
        super(Image.class);
    }
}
