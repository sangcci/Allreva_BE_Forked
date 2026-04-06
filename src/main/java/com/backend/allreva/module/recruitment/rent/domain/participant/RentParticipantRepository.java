package com.backend.allreva.module.recruitment.rent.domain.participant;

import com.backend.allreva.module.recruitment.rent.application.dto.JoinedRentCountResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RentParticipantRepository {

    Optional<RentParticipant> findById(Long id);

    Optional<RentParticipant> findByMemberIdAndBoardingDateAndRentId(
            Long memberId, LocalDate boardingDate, Long rentId);

    RentParticipant save(RentParticipant participant);

    void delete(RentParticipant participant);

    boolean exists(Long memberId, Long rentId, LocalDate boardingDate);

    List<RentParticipant> findAllByRentIdAndBoardingDate(Long rentId, LocalDate boardingDate);

    List<LocalDate> findAppliedBoardingDates(Long memberId, Long rentId);

    List<RentParticipant> findAllByMemberId(Long memberId);

    Optional<JoinedRentCountResponse> findJoinedRentCount(Long memberId, LocalDate boardingDate, Long rentId);
}
