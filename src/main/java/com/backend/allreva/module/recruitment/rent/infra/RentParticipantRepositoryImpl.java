package com.backend.allreva.module.recruitment.rent.infra;

import com.backend.allreva.module.recruitment.rent.domain.participant.RentParticipant;
import com.backend.allreva.module.recruitment.rent.domain.participant.RentParticipantRepository;
import com.backend.allreva.module.recruitment.rent.infra.jpa.RentParticipantJpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RentParticipantRepositoryImpl implements RentParticipantRepository {

    private final RentParticipantJpaRepository rentParticipantJpaRepository;

    @Override
    public RentParticipant save(final RentParticipant participant) {
        return rentParticipantJpaRepository.save(participant);
    }

    @Override
    public Optional<RentParticipant> findById(final Long id) {
        return rentParticipantJpaRepository.findById(id);
    }

    @Override
    public Optional<RentParticipant> findByMemberIdAndBoardingDateAndRentId(
            final Long memberId, final LocalDate boardingDate, final Long rentId) {
        return rentParticipantJpaRepository.findByMemberIdAndBoardingDateAndRentId(memberId, boardingDate, rentId);
    }

    @Override
    public void delete(final RentParticipant participant) {
        rentParticipantJpaRepository.delete(participant);
    }

    @Override
    public boolean exists(final Long memberId, final Long rentId, final LocalDate boardingDate) {
        return rentParticipantJpaRepository.existsByMemberIdAndRentIdAndBoardingDate(memberId, rentId, boardingDate);
    }

    @Override
    public List<RentParticipant> findAllByRentIdAndBoardingDate(final Long rentId, final LocalDate boardingDate) {
        return rentParticipantJpaRepository.findByRentIdAndBoardingDate(rentId, boardingDate);
    }

    @Override
    public List<LocalDate> findAppliedBoardingDates(final Long memberId, final Long rentId) {
        return rentParticipantJpaRepository.findBoardingDateByMemberIdAndRentId(memberId, rentId);
    }

    @Override
    public List<RentParticipant> findAllByMemberId(final Long memberId) {
        return rentParticipantJpaRepository.findAllByMemberId(memberId);
    }
}
