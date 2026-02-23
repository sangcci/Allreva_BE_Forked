package com.backend.allreva.module.concert.artist.infra.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.allreva.module.concert.artist.domain.Artist;

public interface ArtistJpaRepository extends JpaRepository<Artist, String> {

}
