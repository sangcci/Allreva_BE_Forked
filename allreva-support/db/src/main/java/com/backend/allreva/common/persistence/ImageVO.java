package com.backend.allreva.common.persistence;

import com.backend.allreva.common.model.Image;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageVO {

    private String url;

    private ImageVO(final String url) {
        this.url = url;
    }

    public static ImageVO from(final Image image) {
        return image == null ? null : new ImageVO(image.getUrl());
    }

    public Image toDomain() {
        return new Image(url);
    }
}
