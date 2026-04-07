package com.backend.allreva.module.recruitment.rent.infra.jpa;

import com.backend.allreva.module.recruitment.rent.domain.participant.RentParticipant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentParticipantJpaRepository extends JpaRepository<RentParticipant, Long> {

    List<RentParticipant> findByRent_IdAndBoardingDate(Long rentId, LocalDate boardingDate);

    Optional<RentParticipant> findByMemberIdAndBoardingDateAndRent_Id(
            Long memberId, LocalDate boardingDate, Long rentId);

    boolean existsByMemberIdAndRent_IdAndBoardingDate(Long memberId, Long rentId, LocalDate boardingDate);

    @Query("SELECT p.boardingDate FROM RentParticipant p WHERE p.memberId = :memberId AND p.rent.id = :rentId")
    List<LocalDate> findBoardingDateByMemberIdAndRent_Id(
            @Param("memberId") Long memberId, @Param("rentId") Long rentId);

    List<RentParticipant> findAllByMemberId(Long memberId);
}
