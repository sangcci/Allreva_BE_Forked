package com.backend.allreva.module.recruitment.rent.infra;

import static com.backend.allreva.module.concert.concert.domain.QConcert.concert;
import static com.backend.allreva.module.concert.place.domain.QConcertHall.concertHall;
import static com.backend.allreva.module.recruitment.rent.domain.QRent.rent;
import static com.backend.allreva.module.recruitment.rent.domain.QRentBoardingInfo.rentBoardingInfo;
import static com.backend.allreva.rent_join.command.domain.QRentJoin.rentJoin;

import com.backend.allreva.module.recruitment.rent.application.dto.RentAdminSummaryResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentDetailResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentDetailResponse.RentBoardingDateResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentSummaryResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.SortType;
import com.backend.allreva.module.recruitment.rent.domain.Rent;
import com.backend.allreva.module.recruitment.rent.domain.RentBoardingInfo;
import com.backend.allreva.module.recruitment.rent.domain.RentRepository;
import com.backend.allreva.module.recruitment.rent.domain.value.Region;
import com.backend.allreva.module.recruitment.rent.infra.jpa.RentBoardingInfoJpaRepository;
import com.backend.allreva.module.recruitment.rent.infra.jpa.RentJpaRepository;
import com.backend.allreva.rent_join.command.domain.value.BoardingType;
import com.backend.allreva.rent_join.command.domain.value.RefundType;
import com.backend.allreva.rent_join.query.response.RentJoinCountResponse;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RentRepositoryImpl implements RentRepository {

    private final RentJpaRepository rentJpaRepository;
    private final RentBoardingInfoJpaRepository rentBoardingInfoJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Rent> findById(final Long id) {
        return rentJpaRepository.findById(id);
    }

    @Override
    public Optional<Rent> findByIdAndMemberId(final Long rentId, final Long memberId) {
        return rentJpaRepository.findByIdAndMemberId(rentId, memberId);
    }

    @Override
    public Optional<RentBoardingInfo> findByIdAndBoardingDate(
            final Long rentId,
            final LocalDate date
    ) {
        return rentBoardingInfoJpaRepository.findByRentIdAndDate(rentId, date);
    }

    @Override
    public Rent save(final Rent rentEntity) {
        return rentJpaRepository.save(rentEntity);
    }

    @Override
    public void deleteBoardingInfoAllByRentId(final Long rentId) {
        rentBoardingInfoJpaRepository.deleteAllByRentId(rentId);
    }

    @Override
    public void delete(final Rent rentEntity) {
        rentJpaRepository.delete(rentEntity);
    }

    @Override
    public List<RentSummaryResponse> findRentSummaries(
            final Region region,
            final SortType sortType,
            final LocalDate lastEndDate,
            final Long lastId,
            final int pageSize
    ) {
        return queryFactory
                .select(Projections.constructor(RentSummaryResponse.class,
                        rent.id,
                        rent.title,
                        rent.boardingArea,
                        rent.endDate,
                        rent.image.url))
                .from(rent)
                .where(
                        rent.isClosed.eq(false),
                        getRegionCondition(region),
                        getPagingCondition(sortType, lastId, lastEndDate))
                .groupBy(rent.id)
                .orderBy(orderSpecifiers(sortType))
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<RentAdminSummaryResponse> findRentAdminSummaries(
            final Long memberId,
            final Long lastId,
            final int pageSize
    ) {
        return queryFactory
                .select(Projections.constructor(RentAdminSummaryResponse.class,
                        rent.id,
                        rent.title,
                        rentBoardingInfo.date,
                        rent.boardingArea,
                        rent.createdAt,
                        rent.endDate,
                        rentBoardingInfo.recruitmentCount,
                        rentBoardingInfo.passengerCount,
                        rent.isClosed,
                        rent.bus.busSize,
                        rent.bus.busType,
                        rent.bus.maxPassenger))
                .from(rent)
                .join(rentBoardingInfo)
                .on(rent.id.eq(rentBoardingInfo.rent.id))
                .where(
                        rent.memberId.eq(memberId),
                        getPagingCondition(SortType.LATEST, lastId, null))
                .orderBy(orderSpecifiers(SortType.LATEST))
                .limit(pageSize)
                .fetch();
    }

    @Override
    public Optional<RentDetailResponse> findRentDetail(final Long rentId) {
        return Optional.ofNullable(queryFactory
                .select(rentDetailProjections())
                .from(rent)
                .join(rentBoardingInfo).on(rent.id.eq(rentBoardingInfo.rent.id))
                .leftJoin(concert).on(rent.concertId.eq(concert.id))
                .leftJoin(concertHall).on(concert.code.hallCode.eq(concertHall.id))
                .where(rent.id.eq(rentId))
                .fetchFirst());
    }

    @Override
    public Optional<RentJoinCountResponse> findRentJoinCount(
            final Long memberId,
            final LocalDate boardingDate,
            final Long rentId
    ) {
        RentJoinCountResponse rentJoinCountResponse = queryFactory.select(
                Projections.constructor(RentJoinCountResponse.class,
                        getRentBoardingCount(BoardingType.UP, "RentUpCount"),
                        getRentBoardingCount(BoardingType.DOWN, "RentDownCount"),
                        getRentBoardingCount(BoardingType.ROUND, "RentRoundCount"),
                        getRefundCount(RefundType.REFUND, "refundCount"),
                        getRefundCount(RefundType.ADDITIONAL_DEPOSIT, "additionalDepositCount")))
                .from(rentJoin)
                .join(rent).on(rentJoin.rentId.eq(rent.id))
                .where(
                        rentJoin.rentId.eq(rentId),
                        rentJoin.boardingDate.eq(boardingDate),
                        rent.memberId.eq(memberId))
                .groupBy(rentJoin.boardingType, rentJoin.refundType)
                .fetchFirst();
        return Optional.ofNullable(rentJoinCountResponse);
    }

    private ConstructorExpression<RentDetailResponse> rentDetailProjections() {
        return Projections.constructor(RentDetailResponse.class,
                concert.concertInfo.title,
                rent.image.url,
                rent.title,
                rent.artistName,
                rent.region,
                rent.boardingArea,
                concertHall.name,
                rent.upTime,
                rent.downTime,
                Projections.list(
                        Projections.constructor(RentBoardingDateResponse.class,
                                rentBoardingInfo.date,
                                rentBoardingInfo.passengerCount)),
                rent.bus.busSize,
                rent.bus.busType,
                rent.bus.maxPassenger,
                rent.price.roundPrice,
                rent.price.upTimePrice,
                rent.price.downTimePrice,
                rentBoardingInfo.recruitmentCount,
                rent.endDate,
                rent.chatUrl,
                rent.refundType,
                rent.information,
                rent.isClosed);
    }

    private BooleanExpression getRegionCondition(final Region region) {
        return region == null ? null : rent.region.eq(region);
    }

    private BooleanExpression getPagingCondition(
            final SortType sortType,
            final Long lastId,
            final LocalDate lastEndDate) {
        if (lastId == null && lastEndDate == null) {
            return null;
        }

        switch (sortType) {
            case CLOSING -> {
                return (rent.endDate.gt(lastEndDate))
                        .or(rent.endDate.eq(lastEndDate).and(rent.id.gt(lastId)));
            }
            case OLDEST -> {
                return rent.id.gt(lastId);
            }
            default -> {
                return rent.id.lt(lastId);
            }
        }
    }

    private OrderSpecifier<?>[] orderSpecifiers(final SortType sortType) {
        switch (sortType) {
            case CLOSING -> {
                return new OrderSpecifier[]{
                        rent.endDate.asc(),
                        rent.id.asc()
                };
            }
            case OLDEST -> {
                return new OrderSpecifier[]{
                        rent.id.asc()
                };
            }
            default -> {
                return new OrderSpecifier[]{
                        rent.id.desc()
                };
            }
        }
    }

    private NumberExpression<Integer> getRentBoardingCount(final BoardingType boardingType, final String alias) {
        return rentJoin.boardingType
                .when(boardingType)
                .then(rentJoin.passengerNum.sumAggregate().intValue())
                .otherwise(0)
                .as(alias);
    }

    private NumberExpression<Integer> getRefundCount(final RefundType refundType, final String alias) {
        return rentJoin.refundType
                .when(refundType)
                .then(rentJoin.passengerNum.sumAggregate().intValue())
                .otherwise(0)
                .add(rentJoin.refundType
                        .when(RefundType.BOTH)
                        .then(rentJoin.passengerNum.sumAggregate().intValue())
                        .otherwise(0))
                .as(alias);
    }
}
