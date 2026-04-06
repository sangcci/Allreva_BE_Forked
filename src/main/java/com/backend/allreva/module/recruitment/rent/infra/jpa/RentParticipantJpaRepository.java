package com.backend.allreva.module.recruitment.rent.infra.jpa;

import com.backend.allreva.module.recruitment.rent.domain.participant.RentParticipant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentParticipantJpaRepository extends JpaRepository<RentParticipant, Long> {

    List<RentParticipant> findByRentIdAndBoardingDate(Long rentId, LocalDate boardingDate);

    Optional<RentParticipant> findByMemberIdAndBoardingDateAndRentId(
            Long memberId, LocalDate boardingDate, Long rentId);

    boolean existsByMemberIdAndRentIdAndBoardingDate(Long memberId, Long rentId, LocalDate boardingDate);

    List<LocalDate> findBoardingDateByMemberIdAndRentId(Long memberId, Long rentId);

    List<RentParticipant> findAllByMemberId(Long memberId);
}
