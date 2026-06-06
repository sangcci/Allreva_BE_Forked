package com.backend.allreva.recruitment.rent.command.implementation;

import com.backend.allreva.recruitment.rent.domain.Rent;
import com.backend.allreva.recruitment.rent.domain.RentParticipant;
import com.backend.allreva.recruitment.rent.domain.RentParticipantRepository;
import com.backend.allreva.recruitment.rent.domain.RentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RentWriter {

    private final RentRepository rentRepository;
    private final RentParticipantRepository rentParticipantRepository;

    @Transactional
    public Rent save(final Rent rent) {
        return rentRepository.save(rent);
    }

    @Transactional
    public void delete(final Rent rent) {
        rentRepository.delete(rent);
    }

    @Transactional
    public RentParticipant save(final RentParticipant participant) {
        return rentParticipantRepository.save(participant);
    }

    @Transactional
    public void delete(final RentParticipant participant) {
        rentParticipantRepository.delete(participant);
    }
}
