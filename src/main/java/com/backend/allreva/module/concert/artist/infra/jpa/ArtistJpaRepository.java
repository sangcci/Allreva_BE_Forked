package com.backend.allreva.module.concert.artist.infra.jpa;

import com.backend.allreva.module.concert.artist.domain.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistJpaRepository extends JpaRepository<Artist, String> {}
