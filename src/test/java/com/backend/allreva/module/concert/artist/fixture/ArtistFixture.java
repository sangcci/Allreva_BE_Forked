package com.backend.allreva.module.concert.artist.fixture;

import com.backend.allreva.module.concert.artist.domain.Artist;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ArtistFixture {

    public static Artist createArtist(String id, String name) {
        return Artist.builder()
                .id(id)
                .name(name)
                .build();
    }

    public static Artist createArtist(String id) {
        return Artist.builder()
                .id(id)
                .name("테스트 아티스트")
                .build();
    }
}
