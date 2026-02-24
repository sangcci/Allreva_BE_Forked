package com.backend.allreva.module.recruitment.rent.infra.jpa;

import com.backend.allreva.module.recruitment.rent.domain.RentBoardingSlot;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RentBoardingSlotJpaRepository extends JpaRepository<RentBoardingSlot, Long> {

    Optional<RentBoardingSlot> findByRentIdAndDate(Long rentId, LocalDate date);

    List<RentBoardingSlot> findAllByRentId(Long rentId);

    @Modifying
    @Query("DELETE FROM RentBoardingSlot s WHERE s.rentId = :rentId")
    void deleteAllByRentId(Long rentId);
}
