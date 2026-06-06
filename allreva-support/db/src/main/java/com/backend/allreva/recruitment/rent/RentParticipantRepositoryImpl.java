package com.backend.allreva.recruitment.rent;

import com.backend.allreva.recruitment.rent.domain.RentParticipant;
import com.backend.allreva.recruitment.rent.domain.RentParticipantRepository;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RentParticipantRepositoryImpl implements RentParticipantRepository {

    private final RentParticipantJpaRepository rentParticipantJpaRepository;
    private final RentJpaRepository rentJpaRepository;

    @Override
    public RentParticipant save(final RentParticipant participant) {
        return rentParticipantJpaRepository
                .save(RentParticipantEntity.from(
                        participant, rentJpaRepository.getReferenceById(participant.getRentId())))
                .toDomain();
    }

    @Override
    public Optional<RentParticipant> findById(final Long id) {
        return rentParticipantJpaRepository.findById(id).map(RentParticipantEntity::toDomain);
    }

    @Override
    public void delete(final RentParticipant participant) {
        rentParticipantJpaRepository.findById(participant.getId()).ifPresent(rentParticipantJpaRepository::delete);
    }

    @Override
    public boolean exists(final Long memberId, final Long rentId, final LocalDate boardingDate) {
        return rentParticipantJpaRepository.existsByMemberIdAndRent_IdAndBoardingDate(memberId, rentId, boardingDate);
    }
}
