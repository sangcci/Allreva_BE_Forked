package com.backend.allreva.module.concert.artist.fixture;

import com.backend.allreva.module.concert.artist.application.dto.ArtistCreateRequest;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ArtistRequestFixture {

    public static ArtistCreateRequest createArtistCreateRequest(String artistId, String name) {
        return new ArtistCreateRequest(artistId, name);
    }

    public static ArtistCreateRequest createArtistCreateRequest(String artistId) {
        return new ArtistCreateRequest(artistId, "테스트 아티스트");
    }

    public static List<ArtistCreateRequest> createArtistCreateRequests() {
        return List.of(
                new ArtistCreateRequest("artist-1", "아티스트1"),
                new ArtistCreateRequest("artist-2", "아티스트2"),
                new ArtistCreateRequest("artist-3", "아티스트3"));
    }
}
