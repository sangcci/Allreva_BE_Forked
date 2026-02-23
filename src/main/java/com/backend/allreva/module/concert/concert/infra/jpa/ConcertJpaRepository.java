package com.backend.allreva.module.concert.concert.infra.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.allreva.module.concert.concert.application.dto.ConcertDateInfoResponse;
import com.backend.allreva.module.concert.concert.domain.Concert;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {

    boolean existsByCodeConcertCode(String concertCode);

    Concert findByCodeConcertCode(String concertCode);

    @Query("SELECT c.concertInfo.dateInfo.startDate AS startDate, c.concertInfo.dateInfo.endDate AS endDate FROM Concert c WHERE c.id = :concertId")
    Optional<ConcertDateInfoResponse> findStartDateAndEndDateById(@Param("concertId") Long concertId);
}
