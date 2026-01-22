package com.backend.allreva.artist.infra.spotify;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.backend.allreva.artist.query.application.response.SpotifyTokenResponse;

@FeignClient(name = "spotifyAccount", url = "https://accounts.spotify.com")
public interface SpotifyAccountClient {
    @PostMapping(value = "/api/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    SpotifyTokenResponse getAccessToken(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("grant_type") String grantType);
}
