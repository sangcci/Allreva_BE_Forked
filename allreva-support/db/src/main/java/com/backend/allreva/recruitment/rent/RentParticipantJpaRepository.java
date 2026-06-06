package com.backend.allreva.recruitment.rent;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentParticipantJpaRepository extends JpaRepository<RentParticipantEntity, Long> {

    List<RentParticipantEntity> findByRent_IdAndBoardingDate(Long rentId, LocalDate boardingDate);

    boolean existsByMemberIdAndRent_IdAndBoardingDate(Long memberId, Long rentId, LocalDate boardingDate);
}
