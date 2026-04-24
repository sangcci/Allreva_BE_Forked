package com.backend.allreva.module.concert.place.infra.jpa;

import com.backend.allreva.module.concert.place.domain.ConcertHall;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConcertHallJpaRepository extends JpaRepository<ConcertHall, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ConcertHall c WHERE c.id = :hallId")
    Optional<ConcertHall> findByIdWithLock(@Param("hallId") String hallId);

    @Query("SELECT c.id FROM ConcertHall c")
    List<String> findAllIds();

    @Query("SELECT DISTINCT SUBSTRING(c.id, 1, LOCATE('-', c.id) - 1) FROM ConcertHall c WHERE c.id LIKE '%-%'")
    List<String> findAllFacilityCodes();

    @Query("SELECT c.id FROM ConcertHall c WHERE c.id LIKE CONCAT(:facilityCode, '-%')")
    List<String> findIdsByFacilityCode(@Param("facilityCode") String facilityCode);
}
