package com.backend.allreva.module.concert.concert.infra.jpa;

import com.backend.allreva.module.concert.concert.application.dto.ConcertDateInfoResponse;
import com.backend.allreva.module.concert.concert.domain.Concert;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConcertJpaRepository extends JpaRepository<Concert, String> {

    @Query("SELECT c.concertInfo.dateInfo.startDate AS startDate, "
            + "c.concertInfo.dateInfo.endDate AS endDate "
            + "FROM Concert c WHERE c.concertCode = :concertCode")
    Optional<ConcertDateInfoResponse> findStartDateAndEndDateByConcertCode(@Param("concertCode") String concertCode);
}
