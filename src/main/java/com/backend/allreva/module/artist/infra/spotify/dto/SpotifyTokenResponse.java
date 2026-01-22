package com.backend.allreva.module.artist.infra.spotify.dto;

public record SpotifyTokenResponse(
        String access_token,
        String token_type,
        int expires_in) {
}
