package com.backend.allreva.module.artist.infra.spotify.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Image(
        String url,
        int height,
        int width) {
}
