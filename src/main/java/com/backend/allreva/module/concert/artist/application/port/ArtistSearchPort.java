package com.backend.allreva.module.concert.artist.application.port;

import com.backend.allreva.module.concert.artist.application.dto.ArtistSearchResponse;
import java.util.List;

public interface ArtistSearchPort {

    List<ArtistSearchResponse> searchArtist(String query);
}
