package com.backend.allreva.module.artist.infra.spotify;

import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.artist.application.dto.ArtistSearchResponse;
import com.backend.allreva.module.artist.application.dto.ArtistSearchResponse.ArtistImage;
import com.backend.allreva.module.artist.application.port.ArtistSearchPort;
import com.backend.allreva.module.artist.exception.ArtistErrorCode;
import com.backend.allreva.module.artist.infra.spotify.dto.SpotifyArtistWrapper;
import com.backend.allreva.module.artist.infra.spotify.dto.SpotifySearchResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SpotifyAdapter implements ArtistSearchPort {
    private final SpotifyClient spotifyClient;
    private final SpotifyAccountClient spotifyAccountClient;

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    @Override
    public List<ArtistSearchResponse> searchArtist(final String query) {
        String token = "Bearer " + getAccessToken();
        SpotifyArtistWrapper response = spotifyClient.searchArtists(token, query);

        List<SpotifySearchResponse> spotifyArtists = response.artists().items();

        if (spotifyArtists.isEmpty()) {
            throw new CustomException(ArtistErrorCode.ARTIST_SEARCH_NO_CONTENT);
        }

        return spotifyArtists.stream()
                .map(this::toArtistSearchResponse)
                .toList();
    }

    private ArtistSearchResponse toArtistSearchResponse(SpotifySearchResponse spotify) {
        List<ArtistImage> images = spotify.images().stream()
                .map(img -> new ArtistImage(img.url(), img.height(), img.width()))
                .toList();

        return new ArtistSearchResponse(spotify.id(), spotify.name(), images);
    }

    private String getAccessToken() {
        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder()
                .encodeToString(credentials.getBytes());

        return spotifyAccountClient.getAccessToken(
                "Basic " + encodedCredentials, "client_credentials")
                .access_token();
    }
}
