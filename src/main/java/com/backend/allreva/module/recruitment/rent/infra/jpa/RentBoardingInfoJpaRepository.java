package com.backend.allreva.module.recruitment.rent.infra.jpa;

import com.backend.allreva.module.recruitment.rent.domain.RentBoardingInfo;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentBoardingInfoJpaRepository extends JpaRepository<RentBoardingInfo, Long> {

    Optional<RentBoardingInfo> findByRentIdAndDate(Long rentId, LocalDate date);

    @Modifying
    @Query("DELETE FROM RentBoardingInfo rfbd WHERE rfbd.rent.id = :rentId")
    void deleteAllByRentId(@Param("rentId") Long rentId);
}
