package com.backend.allreva.module.concert.artist.infra;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.allreva.module.concert.artist.domain.Artist;
import com.backend.allreva.module.concert.artist.domain.ArtistRepository;
import com.backend.allreva.module.concert.artist.infra.jpa.ArtistJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ArtistRepositoryImpl implements ArtistRepository {

    private final ArtistJpaRepository jpa;

    @Override
    public List<Artist> findAllById(final Iterable<String> ids) {
        return jpa.findAllById(ids);
    }

    @Override
    public List<Artist> saveAll(final Iterable<Artist> artists) {
        return jpa.saveAll(artists);
    }

    @Override
    public Optional<Artist> findById(final String id) {
        return jpa.findById(id);
    }
}
