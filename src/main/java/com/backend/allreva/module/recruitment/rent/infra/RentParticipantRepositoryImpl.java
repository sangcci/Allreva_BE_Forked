package com.backend.allreva.module.recruitment.rent.infra;

import static com.backend.allreva.module.recruitment.rent.domain.QRent.rent;
import static com.backend.allreva.module.recruitment.rent.domain.participant.QRentParticipant.rentParticipant;

import com.backend.allreva.module.recruitment.rent.domain.participant.RentParticipant;
import com.backend.allreva.module.recruitment.rent.domain.participant.RentParticipantRepository;
import com.backend.allreva.module.recruitment.rent.infra.jpa.RentParticipantJpaRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RentParticipantRepositoryImpl implements RentParticipantRepository {

    private final RentParticipantJpaRepository rentParticipantJpaRepository;
    private final JPAQueryFactory queryFactory;

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
        return rentParticipantJpaRepository.findByMemberIdAndBoardingDateAndRent_Id(memberId, boardingDate, rentId);
    }

    @Override
    public void delete(final RentParticipant participant) {
        rentParticipantJpaRepository.delete(participant);
    }

    @Override
    public boolean exists(final Long memberId, final Long rentId, final LocalDate boardingDate) {
        return rentParticipantJpaRepository.existsByMemberIdAndRent_IdAndBoardingDate(memberId, rentId, boardingDate);
    }

    @Override
    public List<RentParticipant> findAllByRentIdAndBoardingDate(final Long rentId, final LocalDate boardingDate) {
        return rentParticipantJpaRepository.findByRent_IdAndBoardingDate(rentId, boardingDate);
    }

    @Override
    public List<LocalDate> findAppliedBoardingDates(final Long memberId, final Long rentId) {
        return rentParticipantJpaRepository.findBoardingDateByMemberIdAndRent_Id(memberId, rentId);
    }

    @Override
    public List<RentParticipant> findAllByMemberId(final Long memberId, final Long lastId, final int pageSize) {
        return queryFactory
                .selectFrom(rentParticipant)
                .join(rentParticipant.rent, rent)
                .fetchJoin()
                .where(rentParticipant.memberId.eq(memberId), getCursorCondition(lastId))
                .orderBy(rentParticipant.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression getCursorCondition(final Long lastId) {
        return lastId == null ? null : rentParticipant.id.lt(lastId);
    }
}
