package com.backend.allreva.module.artist.application.port;

import java.util.List;

import com.backend.allreva.module.artist.application.dto.ArtistSearchResponse;

public interface ArtistSearchPort {

    List<ArtistSearchResponse> searchArtist(String query);
}
