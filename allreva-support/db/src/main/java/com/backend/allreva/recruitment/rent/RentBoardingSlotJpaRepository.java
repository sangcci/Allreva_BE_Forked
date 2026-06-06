package com.backend.allreva.recruitment.rent;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentBoardingSlotJpaRepository extends JpaRepository<RentBoardingSlotEntity, Long> {

    @Modifying
    @Query("""
        UPDATE RentBoardingSlot s
        SET s.passengerCount = s.passengerCount + :count
        WHERE s.rent.id = :rentId
          AND s.date = :boardingDate
          AND s.passengerCount + :count <= s.recruitmentCount
        """)
    int incrementPassengerCount(
            @Param("rentId") Long rentId, @Param("boardingDate") LocalDate boardingDate, @Param("count") int count);

    @Modifying
    @Query("""
        UPDATE RentBoardingSlot s
        SET s.passengerCount = s.passengerCount - :count
        WHERE s.rent.id = :rentId
          AND s.date = :boardingDate
          AND s.passengerCount - :count >= 0
        """)
    int decrementPassengerCount(
            @Param("rentId") Long rentId, @Param("boardingDate") LocalDate boardingDate, @Param("count") int count);
}
