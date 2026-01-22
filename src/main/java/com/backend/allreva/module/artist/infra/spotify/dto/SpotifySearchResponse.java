package com.backend.allreva.module.artist.infra.spotify.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifySearchResponse(
        String id,
        String name,
        List<Image> images) {
}
