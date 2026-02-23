package com.backend.allreva.module.concert.artist.infra.spotify.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyArtistWrapper(
        Artists artists) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Artists(
            List<SpotifySearchResponse> items) {
    }
}
