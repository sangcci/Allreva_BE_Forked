package com.backend.allreva.module.recruitment.rent.infra;

import static com.backend.allreva.module.recruitment.rent.domain.QRent.rent;
import static com.backend.allreva.module.recruitment.rent.domain.participant.QRentParticipant.rentParticipant;

import com.backend.allreva.module.recruitment.rent.application.dto.JoinedRentCountResponse;
import com.backend.allreva.module.recruitment.rent.domain.participant.RentParticipant;
import com.backend.allreva.module.recruitment.rent.domain.participant.RentParticipantRepository;
import com.backend.allreva.module.recruitment.rent.domain.value.BoardingType;
import com.backend.allreva.module.recruitment.rent.domain.value.RefundType;
import com.backend.allreva.module.recruitment.rent.infra.jpa.RentParticipantJpaRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
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

    @Override
    public Optional<JoinedRentCountResponse> findJoinedRentCount(
            final Long memberId, final LocalDate boardingDate, final Long rentId) {
        JoinedRentCountResponse result = queryFactory
                .select(Projections.constructor(
                        JoinedRentCountResponse.class,
                        getRentBoardingCount(BoardingType.UP, "rentUpCount"),
                        getRentBoardingCount(BoardingType.DOWN, "rentDownCount"),
                        getRentBoardingCount(BoardingType.ROUND, "rentRoundCount"),
                        getRefundCount(RefundType.REFUND, "refundCount"),
                        getRefundCount(RefundType.ADDITIONAL_DEPOSIT, "additionalDepositCount")))
                .from(rentParticipant)
                .join(rent)
                .on(rentParticipant.rentId.eq(rent.id))
                .where(
                        rentParticipant.rentId.eq(rentId),
                        rentParticipant.boardingDate.eq(boardingDate),
                        rent.memberId.eq(memberId))
                .fetchFirst();
        return Optional.ofNullable(result);
    }

    private NumberExpression<Integer> getRentBoardingCount(final BoardingType boardingType, final String alias) {
        return rentParticipant
                .boardingType
                .when(boardingType)
                .then(rentParticipant.passengerNum.sumAggregate().intValue())
                .otherwise(0)
                .as(alias);
    }

    private NumberExpression<Integer> getRefundCount(final RefundType refundType, final String alias) {
        return rentParticipant
                .refundType
                .when(refundType)
                .then(rentParticipant.passengerNum.sumAggregate().intValue())
                .otherwise(0)
                .add(rentParticipant
                        .refundType
                        .when(RefundType.BOTH)
                        .then(rentParticipant.passengerNum.sumAggregate().intValue())
                        .otherwise(0))
                .as(alias);
    }
}
