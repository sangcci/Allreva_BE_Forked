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

    List<RentBoardingSlot> findAllByRent_Id(Long rentId);

    Optional<RentBoardingSlot> findByRent_IdAndDate(Long rentId, LocalDate date);

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
