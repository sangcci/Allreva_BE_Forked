package com.backend.allreva.recruitment.rent.command.implementation;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.recruitment.rent.domain.RentErrorCode;
import com.backend.allreva.recruitment.rent.domain.RentParticipant;
import com.backend.allreva.recruitment.rent.domain.RentParticipantRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentParticipantReader {

    private final RentParticipantRepository rentParticipantRepository;

    public RentParticipant get(final Long id) {
        return rentParticipantRepository
                .findById(id)
                .orElseThrow(() -> new CustomException(RentErrorCode.RENT_JOIN_NOT_FOUND));
    }

    public boolean exists(final Long memberId, final Long rentId, final LocalDate boardingDate) {
        return rentParticipantRepository.exists(memberId, rentId, boardingDate);
    }

    public void validateNotAlreadyJoined(final Long memberId, final Long rentId, final LocalDate boardingDate) {
        if (exists(memberId, rentId, boardingDate)) {
            throw new CustomException(RentErrorCode.RENT_JOIN_ALREADY_EXISTS);
        }
    }
}
