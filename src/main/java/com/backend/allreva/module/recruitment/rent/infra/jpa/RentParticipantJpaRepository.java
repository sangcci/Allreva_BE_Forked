package com.backend.allreva.module.recruitment.rent.infra.jpa;

import com.backend.allreva.module.recruitment.rent.domain.participant.RentParticipant;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentParticipantJpaRepository extends JpaRepository<RentParticipant, Long> {

    List<RentParticipant> findByRentIdAndBoardingDate(Long rentId, LocalDate boardingDate);

    boolean existsByMemberIdAndRentIdAndBoardingDate(Long memberId, Long rentId, LocalDate boardingDate);

    List<LocalDate> findBoardingDateByMemberIdAndRentId(Long memberId, Long rentId);
}
