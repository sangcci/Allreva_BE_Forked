package com.backend.allreva.module.concert.concert.infra.jpa;

import com.backend.allreva.module.concert.concert.domain.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertJpaRepository extends JpaRepository<Concert, String> {}
