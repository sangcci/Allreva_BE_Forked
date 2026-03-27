package com.backend.allreva.module.recruitment.rent.infra.jpa;

import com.backend.allreva.module.recruitment.rent.domain.RentBoardingSlot;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentBoardingSlotJpaRepository extends JpaRepository<RentBoardingSlot, Long> {

    Optional<RentBoardingSlot> findByRentIdAndDate(Long rentId, LocalDate date);

    List<RentBoardingSlot> findAllByRentId(Long rentId);

    @Modifying
    @Query("UPDATE RentBoardingSlot s SET s.deletedAt = CURRENT_TIMESTAMP WHERE s.rentId = :rentId")
    void deleteAllByRentId(@Param("rentId") Long rentId);

    @Modifying
    @Query("""
        UPDATE RentBoardingSlot s
        SET s.passengerCount = s.passengerCount + :count
        WHERE s.rentId = :rentId
          AND s.date = :boardingDate
          AND s.passengerCount + :count <= s.recruitmentCount
        """)
    int incrementPassengerCount(
            @Param("rentId") Long rentId, @Param("boardingDate") LocalDate boardingDate, @Param("count") int count);
}
