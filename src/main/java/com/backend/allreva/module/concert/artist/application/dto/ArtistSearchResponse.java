package com.backend.allreva.module.concert.artist.application.dto;

import java.util.List;

public record ArtistSearchResponse(String id, String name, List<ArtistImage> images) {

    public record ArtistImage(String url, int height, int width) {}
}
