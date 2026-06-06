package com.backend.allreva.recruitment.rent.domain;

import java.time.LocalDate;
import java.util.Optional;

public interface RentParticipantRepository {

    Optional<RentParticipant> findById(Long id);

    RentParticipant save(RentParticipant participant);

    void delete(RentParticipant participant);

    boolean exists(Long memberId, Long rentId, LocalDate boardingDate);
}
