package com.backend.allreva.recruitment.rent;

import static com.backend.allreva.concert.concert.QConcertEntity.concertEntity;
import static com.backend.allreva.recruitment.rent.QRentBoardingSlotEntity.rentBoardingSlotEntity;
import static com.backend.allreva.recruitment.rent.QRentEntity.rentEntity;
import static com.backend.allreva.recruitment.rent.QRentParticipantEntity.rentParticipantEntity;

import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.recruitment.rent.domain.Route;
import com.backend.allreva.recruitment.rent.domain.SortType;
import com.backend.allreva.recruitment.rent.query.implementation.RentFinderPort;
import com.backend.allreva.recruitment.rent.query.model.HostedRentSummary;
import com.backend.allreva.recruitment.rent.query.model.JoinedRent;
import com.backend.allreva.recruitment.rent.query.model.RentDetail;
import com.backend.allreva.recruitment.rent.query.model.RentParticipantItem;
import com.backend.allreva.recruitment.rent.query.model.RentSummary;
import com.backend.allreva.recruitment.rent.query.model.RentThumbnail;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class RentFinderAdapter implements RentFinderPort {

    private static final double SIMILARITY_THRESHOLD = 0.1;

    private final JPAQueryFactory queryFactory;
    private final RentJpaRepository rentJpaRepository;
    private final RentParticipantJpaRepository rentParticipantJpaRepository;

    @Override
    public List<RentThumbnail> findThumbnailsByTitle(final String title, final int limit) {
        return fetchRents(titleMatchCondition(title), null, limit);
    }

    @Override
    public SliceResponse<RentThumbnail, Long> findAllByTitle(
            final String query, final Long cursorId, final int pageSize) {
        BooleanExpression notExpired = rentEntity.endDate.goe(LocalDate.now());
        BooleanExpression titleMatch = titleMatchCondition(query);
        BooleanExpression cursor = cursorId != null ? rentEntity.id.lt(cursorId) : null;

        List<RentThumbnail> results =
                fetchRents(titleMatch != null ? titleMatch.and(notExpired) : notExpired, cursor, pageSize + 1);
        Long nextCursorId =
                results.size() > pageSize ? results.get(pageSize - 1).id() : null;
        return new SliceResponse<>(results.stream().limit(pageSize).toList(), nextCursorId);
    }

    @Override
    public List<RentSummary> findRentSummaries(
            final String region,
            final SortType sortType,
            final LocalDate lastEndDate,
            final Long lastId,
            final int pageSize) {
        List<Long> ids = queryFactory
                .select(rentEntity.id)
                .from(rentEntity)
                .where(
                        rentEntity.isClosed.eq(false),
                        regionCondition(region),
                        pagingCondition(sortType, lastId, lastEndDate))
                .orderBy(orderSpecifiers(sortType))
                .limit(pageSize)
                .fetch();

        return sortByIds(ids, rentJpaRepository.findAllByIdIn(ids)).stream()
                .map(rent -> new RentSummary(
                        rent.getId(),
                        rent.getTitle(),
                        rent.getRegion(),
                        Route.builder()
                                .boardingArea(rent.getUpBoardingArea())
                                .dropOffArea(rent.getUpDropOffArea())
                                .time(rent.getUpTime())
                                .build(),
                        Route.builder()
                                .boardingArea(rent.getDownBoardingArea())
                                .dropOffArea(rent.getDownDropOffArea())
                                .time(rent.getDownTime())
                                .build(),
                        rent.getEndDate(),
                        rent.getImageUrl()))
                .toList();
    }

    @Override
    public Optional<RentDetail> findRentDetail(final Long rentId) {
        Tuple rent = queryFactory
                .select(
                        concertEntity.title,
                        rentEntity.imageUrl,
                        rentEntity.title,
                        concertEntity.castNames,
                        rentEntity.region,
                        rentEntity.boardingType,
                        rentEntity.upBoardingArea,
                        rentEntity.upDropOffArea,
                        rentEntity.upTime,
                        rentEntity.downBoardingArea,
                        rentEntity.downDropOffArea,
                        rentEntity.downTime,
                        rentEntity.busSize,
                        rentEntity.busType,
                        rentEntity.maxPassenger,
                        rentEntity.price,
                        rentEntity.endDate,
                        rentEntity.information,
                        rentEntity.isClosed)
                .from(rentEntity)
                .leftJoin(concertEntity)
                .on(rentEntity.concertCode.eq(concertEntity.concertCode))
                .where(rentEntity.id.eq(rentId))
                .fetchOne();

        if (rent == null) {
            return Optional.empty();
        }

        List<RentDetail.RentBoardingDate> boardingDates = queryFactory
                .select(Projections.constructor(
                        RentDetail.RentBoardingDate.class,
                        rentBoardingSlotEntity.date,
                        rentBoardingSlotEntity.passengerCount))
                .from(rentBoardingSlotEntity)
                .where(rentBoardingSlotEntity.rent.id.eq(rentId))
                .orderBy(rentBoardingSlotEntity.date.asc())
                .fetch();

        Integer recruitmentCount = queryFactory
                .select(rentBoardingSlotEntity.recruitmentCount)
                .from(rentBoardingSlotEntity)
                .where(rentBoardingSlotEntity.rent.id.eq(rentId))
                .orderBy(rentBoardingSlotEntity.date.asc())
                .limit(1)
                .fetchOne();

        @SuppressWarnings("unchecked")
        List<String> castNames = rent.get(concertEntity.castNames);

        return Optional.of(new RentDetail(
                rent.get(concertEntity.title),
                rent.get(rentEntity.imageUrl),
                rent.get(rentEntity.title),
                castNames != null ? castNames : Collections.emptyList(),
                rent.get(rentEntity.region),
                rent.get(rentEntity.boardingType),
                Route.builder()
                        .boardingArea(rent.get(rentEntity.upBoardingArea))
                        .dropOffArea(rent.get(rentEntity.upDropOffArea))
                        .time(rent.get(rentEntity.upTime))
                        .build(),
                Route.builder()
                        .boardingArea(rent.get(rentEntity.downBoardingArea))
                        .dropOffArea(rent.get(rentEntity.downDropOffArea))
                        .time(rent.get(rentEntity.downTime))
                        .build(),
                boardingDates,
                rent.get(rentEntity.busSize),
                rent.get(rentEntity.busType),
                rent.get(rentEntity.maxPassenger),
                rent.get(rentEntity.price),
                recruitmentCount != null ? recruitmentCount : 0,
                rent.get(rentEntity.endDate),
                rent.get(rentEntity.information),
                Boolean.TRUE.equals(rent.get(rentEntity.isClosed))));
    }

    @Override
    public List<HostedRentSummary> findHostedRentSummaries(final Long memberId, final Long lastId, final int pageSize) {
        List<Long> ids = queryFactory
                .select(rentEntity.id)
                .from(rentEntity)
                .where(rentEntity.memberId.eq(memberId), pagingCondition(SortType.LATEST, lastId, null))
                .orderBy(orderSpecifiers(SortType.LATEST))
                .limit(pageSize)
                .fetch();

        return sortByIds(ids, rentJpaRepository.findAllByIdIn(ids)).stream()
                .map(this::toHostedRentSummary)
                .toList();
    }

    @Override
    public Optional<List<RentParticipantItem>> findHostedRentParticipants(
            final Long memberId, final Long rentId, final LocalDate boardingDate) {
        Integer exists = queryFactory
                .selectOne()
                .from(rentEntity)
                .join(rentEntity.boardingSlots, rentBoardingSlotEntity)
                .where(
                        rentEntity.id.eq(rentId),
                        rentEntity.memberId.eq(memberId),
                        rentBoardingSlotEntity.date.eq(boardingDate))
                .fetchFirst();

        if (exists == null) {
            return Optional.empty();
        }

        return Optional.of(rentParticipantJpaRepository.findByRent_IdAndBoardingDate(rentId, boardingDate).stream()
                .map(this::toRentParticipantItem)
                .toList());
    }

    @Override
    public List<JoinedRent> findJoinedRents(final Long memberId, final Long lastId, final int pageSize) {
        List<RentParticipantEntity> participants = queryFactory
                .selectFrom(rentParticipantEntity)
                .where(rentParticipantEntity.memberId.eq(memberId), cursorCondition(lastId))
                .orderBy(rentParticipantEntity.id.desc())
                .limit(pageSize)
                .fetch();

        Map<Long, RentEntity> rentsById = rentJpaRepository
                .findAllByIdIn(participants.stream()
                        .map(participant -> participant.getRent().getId())
                        .distinct()
                        .toList())
                .stream()
                .collect(Collectors.toMap(RentEntity::getId, Function.identity()));

        return participants.stream()
                .map(participant -> toJoinedRent(
                        participant, rentsById.get(participant.getRent().getId())))
                .toList();
    }

    @Override
    public Optional<RentParticipantItem> findJoinedRentParticipant(
            final Long memberId, final Long rentId, final LocalDate boardingDate) {
        return Optional.ofNullable(queryFactory
                        .selectFrom(rentParticipantEntity)
                        .where(
                                rentParticipantEntity.memberId.eq(memberId),
                                rentParticipantEntity.rent.id.eq(rentId),
                                rentParticipantEntity.boardingDate.eq(boardingDate))
                        .fetchOne())
                .map(this::toRentParticipantItem);
    }

    private List<RentThumbnail> fetchRents(
            final BooleanExpression condition, final BooleanExpression cursor, final int fetchSize) {
        return queryFactory
                .select(Projections.constructor(
                        RentThumbnail.class,
                        rentEntity.id,
                        rentEntity.title,
                        rentEntity.region,
                        rentEntity.imageUrl,
                        rentEntity.endDate))
                .from(rentEntity)
                .where(condition, cursor)
                .orderBy(rentEntity.id.desc())
                .limit(fetchSize)
                .fetch();
    }

    private BooleanExpression titleMatchCondition(final String query) {
        if (!StringUtils.hasText(query)) {
            return null;
        }
        NumberTemplate<Double> sim =
                Expressions.numberTemplate(Double.class, "similarity({0}, {1})", rentEntity.title, query);
        BooleanExpression ilike = Expressions.booleanTemplate("({0} ilike {1})", rentEntity.title, "%" + query + "%");
        return sim.gt(SIMILARITY_THRESHOLD).or(ilike);
    }

    private HostedRentSummary toHostedRentSummary(final RentEntity rent) {
        return new HostedRentSummary(
                rent.getId(),
                rent.getTitle(),
                rent.getBoardingType(),
                Route.builder()
                        .boardingArea(rent.getUpBoardingArea())
                        .dropOffArea(rent.getUpDropOffArea())
                        .time(rent.getUpTime())
                        .build(),
                Route.builder()
                        .boardingArea(rent.getDownBoardingArea())
                        .dropOffArea(rent.getDownDropOffArea())
                        .time(rent.getDownTime())
                        .build(),
                rent.getCreatedAt(),
                rent.getEndDate(),
                rent.isClosed(),
                rent.getBusSize(),
                rent.getBusType(),
                rent.getMaxPassenger(),
                rent.getBoardingSlots().stream()
                        .map(slot -> new HostedRentSummary.BoardingSlotSummary(
                                slot.getDate(), slot.getRecruitmentCount(), slot.getPassengerCount()))
                        .toList());
    }

    private RentParticipantItem toRentParticipantItem(final RentParticipantEntity participant) {
        return new RentParticipantItem(
                participant.getId(),
                participant.getCreatedAt(),
                participant.getDepositorName(),
                participant.getPhone(),
                participant.getPassengerNum(),
                participant.getDepositorTime(),
                participant.getRefundType(),
                participant.getRefundAccount());
    }

    private JoinedRent toJoinedRent(final RentParticipantEntity participant, final RentEntity rent) {
        RentBoardingSlotEntity slot = rent.getBoardingSlots().stream()
                .filter(boardingSlot -> boardingSlot.getDate().equals(participant.getBoardingDate()))
                .findFirst()
                .orElse(null);

        return new JoinedRent(
                rent.getId(),
                rent.getTitle(),
                participant.getBoardingDate(),
                Route.builder()
                        .boardingArea(rent.getUpBoardingArea())
                        .dropOffArea(rent.getUpDropOffArea())
                        .time(rent.getUpTime())
                        .build(),
                Route.builder()
                        .boardingArea(rent.getDownBoardingArea())
                        .dropOffArea(rent.getDownDropOffArea())
                        .time(rent.getDownTime())
                        .build(),
                rent.getCreatedAt(),
                rent.getEndDate(),
                slot != null ? slot.getRecruitmentCount() : 0,
                slot != null ? slot.getPassengerCount() : 0,
                rent.isClosed(),
                participant.getId(),
                participant.getCreatedAt(),
                participant.getPassengerNum(),
                participant.getDepositorName(),
                participant.getDepositorTime(),
                participant.getRefundType());
    }

    private List<RentEntity> sortByIds(final List<Long> ids, final List<RentEntity> rents) {
        Map<Long, RentEntity> rentsById = rents.stream()
                .collect(Collectors.toMap(
                        RentEntity::getId, Function.identity(), (left, right) -> left, LinkedHashMap::new));
        return ids.stream().map(rentsById::get).toList();
    }

    private static BooleanExpression regionCondition(final String region) {
        return region == null ? null : rentEntity.region.eq(region);
    }

    private BooleanExpression pagingCondition(final SortType sortType, final Long lastId, final LocalDate lastEndDate) {
        if (lastId == null && lastEndDate == null) {
            return null;
        }

        switch (sortType) {
            case CLOSING -> {
                return rentEntity
                        .endDate
                        .gt(lastEndDate)
                        .or(rentEntity.endDate.eq(lastEndDate).and(rentEntity.id.gt(lastId)));
            }
            case OLDEST -> {
                return rentEntity.id.gt(lastId);
            }
            default -> {
                return rentEntity.id.lt(lastId);
            }
        }
    }

    private BooleanExpression cursorCondition(final Long lastId) {
        return lastId == null ? null : rentParticipantEntity.id.lt(lastId);
    }

    private OrderSpecifier<?>[] orderSpecifiers(final SortType sortType) {
        switch (sortType) {
            case CLOSING -> {
                return new OrderSpecifier[] {rentEntity.endDate.asc(), rentEntity.id.asc()};
            }
            case OLDEST -> {
                return new OrderSpecifier[] {rentEntity.id.asc()};
            }
            default -> {
                return new OrderSpecifier[] {rentEntity.id.desc()};
            }
        }
    }
}
