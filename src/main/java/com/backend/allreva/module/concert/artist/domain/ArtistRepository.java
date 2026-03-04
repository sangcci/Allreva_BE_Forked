package com.backend.allreva.module.concert.artist.domain;

import java.util.List;
import java.util.Optional;

public interface ArtistRepository {

    List<Artist> findAllById(Iterable<String> ids);

    List<Artist> saveAll(Iterable<Artist> artists);

    Optional<Artist> findById(String id);
}
