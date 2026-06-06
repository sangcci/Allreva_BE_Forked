package com.backend.allreva.concert.place;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConcertHallJpaRepository extends JpaRepository<ConcertHallEntity, String> {

    @Query("SELECT c.hallCode FROM ConcertHall c")
    List<String> findAllHallCodes();

    @Query(
            "SELECT DISTINCT SUBSTRING(c.hallCode, 1, LOCATE('-', c.hallCode) - 1) FROM ConcertHall c WHERE c.hallCode LIKE '%-%'")
    List<String> findAllFacilityCodes();

    @Query("SELECT c.hallCode FROM ConcertHall c WHERE c.hallCode LIKE CONCAT(:facilityCode, '-%')")
    List<String> findHallCodesByFacilityCode(@Param("facilityCode") String facilityCode);
}
