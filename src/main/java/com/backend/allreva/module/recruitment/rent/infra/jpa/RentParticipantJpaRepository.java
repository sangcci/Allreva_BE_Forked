package com.backend.allreva.module.recruitment.rent.infra.jpa;

import com.backend.allreva.module.recruitment.rent.domain.participant.RentParticipant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentParticipantJpaRepository extends JpaRepository<RentParticipant, Long> {

    List<RentParticipant> findByRent_IdAndBoardingDate(Long rentId, LocalDate boardingDate);

    Optional<RentParticipant> findByMemberIdAndBoardingDateAndRent_Id(
            Long memberId, LocalDate boardingDate, Long rentId);

    boolean existsByMemberIdAndRent_IdAndBoardingDate(Long memberId, Long rentId, LocalDate boardingDate);

    List<RentParticipant> findAllByMemberId(Long memberId);
}
