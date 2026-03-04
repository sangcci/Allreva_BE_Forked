package com.backend.allreva.module.recruitment.rent.domain.participant;

import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinCountResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RentParticipantRepository {

    RentParticipant save(RentParticipant participant);

    Optional<RentParticipant> findById(Long id);

    void delete(RentParticipant participant);

    boolean exists(Long memberId, Long rentId, LocalDate boardingDate);

    List<RentParticipant> findByRentIdAndBoardingDate(Long rentId, LocalDate boardingDate);

    List<LocalDate> findAppliedBoardingDates(Long memberId, Long rentId);

    List<RentJoinResponse> findByMemberId(Long memberId);

    Optional<RentJoinCountResponse> findRentJoinCount(Long memberId, LocalDate boardingDate, Long rentId);
}
