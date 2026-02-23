package com.backend.allreva.module.concert.artist.application.port;

import java.util.List;

import com.backend.allreva.module.concert.artist.application.dto.ArtistSearchResponse;

public interface ArtistSearchPort {

    List<ArtistSearchResponse> searchArtist(String query);
}
