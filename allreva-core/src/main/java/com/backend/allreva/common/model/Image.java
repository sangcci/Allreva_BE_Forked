package com.backend.allreva.common.model;

import java.util.Objects;

public class Image {
    private final String url;

    public Image(final String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Image image)) {
            return false;
        }
        return Objects.equals(url, image.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
