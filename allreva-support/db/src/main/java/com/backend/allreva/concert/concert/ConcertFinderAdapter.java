package com.backend.allreva.concert.concert;

import static com.backend.allreva.concert.concert.QConcertEntity.concertEntity;
import static com.backend.allreva.concert.place.QConcertHallEntity.concertHallEntity;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.concert.concert.domain.ConcertInfo;
import com.backend.allreva.concert.concert.domain.DateInfo;
import com.backend.allreva.concert.concert.domain.SortDirection;
import com.backend.allreva.concert.concert.query.implementation.ConcertFinderPort;
import com.backend.allreva.concert.concert.query.model.ConcertDetail;
import com.backend.allreva.concert.concert.query.model.ConcertThumbnail;
import com.backend.allreva.concert.concert.query.model.RelatedConcert;
import com.backend.allreva.concert.place.ConcertHallEntity;
import com.backend.allreva.concert.place.domain.ConvenienceInfo;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class ConcertFinderAdapter implements ConcertFinderPort {

    private static final double SIMILARITY_THRESHOLD = 0.1;

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ConcertThumbnail> findThumbnailsByTitle(final String title, final int limit) {
        return queryFactory
                .select(Projections.constructor(
                        ConcertThumbnail.class,
                        concertEntity.posterUrl,
                        concertEntity.title,
                        concertHallEntity.name,
                        concertEntity.startDate,
                        concertEntity.endDate,
                        concertEntity.concertCode))
                .from(concertEntity)
                .leftJoin(concertHallEntity)
                .on(concertEntity.hallCode.eq(concertHallEntity.hallCode))
                .where(titleMatchCondition(title))
                .orderBy(similarityOrder(title), concertEntity.concertCode.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public SliceResponse<ConcertThumbnail, String> findAllByTitle(
            final String query, final String cursorCode, final int pageSize) {
        List<ConcertThumbnail> results = fetchConcerts(
                titleMatchCondition(query), cursorCondition(cursorCode), similarityOrder(query), pageSize + 1);
        return buildResponse(results, pageSize);
    }

    @Override
    public SliceResponse<ConcertThumbnail, String> findAllByAddressAndSortDirection(
            final String address, final String cursorCode, final int pageSize, final SortDirection sortDirection) {
        BooleanExpression addressCondition = StringUtils.hasText(address)
                ? Expressions.booleanTemplate("({0} ilike {1})", concertHallEntity.address, "%" + address + "%")
                : null;

        List<ConcertThumbnail> results = fetchConcerts(
                addressCondition, cursorCondition(cursorCode), mainSortOrder(sortDirection), pageSize + 1);
        return buildResponse(results, pageSize);
    }

    @Override
    public Optional<ConcertDetail> findConcertDetail(final String concertCode) {
        Tuple result = queryFactory
                .select(concertEntity, concertHallEntity)
                .from(concertEntity)
                .leftJoin(concertHallEntity)
                .on(concertEntity.hallCode.eq(concertHallEntity.hallCode))
                .where(concertEntity.concertCode.eq(concertCode))
                .fetchOne();
        if (result == null) {
            return Optional.empty();
        }

        ConcertEntity concert = result.get(concertEntity);
        ConcertHallEntity hall = result.get(concertHallEntity);
        return Optional.of(new ConcertDetail(
                concert.getPosterUrl() != null ? new Image(concert.getPosterUrl()) : null,
                concert.getDetailImages().stream()
                        .map(image -> image.toDomain())
                        .toList(),
                ConcertInfo.builder()
                        .title(concert.getTitle())
                        .price(concert.getPrice())
                        .performStatus(concert.getPerformStatus())
                        .host(concert.getHost())
                        .dateInfo(DateInfo.builder()
                                .startDate(concert.getStartDate())
                                .endDate(concert.getEndDate())
                                .timeTable(concert.getTimeTable())
                                .build())
                        .build(),
                concert.getSellers().stream()
                        .map(seller -> seller.toDomain())
                        .collect(java.util.stream.Collectors.toSet()),
                hall != null ? hall.getHallCode() : null,
                hall != null ? hall.getName() : null,
                hall != null ? hall.getSeatScale() : null,
                hall != null
                        ? ConvenienceInfo.builder()
                                .hasParkingLot(hall.isHasParkingLot())
                                .hasRestaurant(hall.isHasRestaurant())
                                .hasCafe(hall.isHasCafe())
                                .hasStore(hall.isHasStore())
                                .hasDisabledParking(hall.isHasDisabledParking())
                                .hasDisabledToilet(hall.isHasDisabledToilet())
                                .hasElevator(hall.isHasElevator())
                                .hasRunway(hall.isHasRunway())
                                .build()
                        : null,
                hall != null ? hall.getAddress() : null));
    }

    @Override
    public List<RelatedConcert> findRelatedConcerts(
            final String hallCode, final String lastConcertCode, final int pageSize) {
        return queryFactory
                .select(Projections.constructor(
                        RelatedConcert.class,
                        concertEntity.concertCode,
                        concertEntity.title,
                        concertEntity.startDate,
                        concertEntity.endDate,
                        concertEntity.posterUrl))
                .from(concertEntity)
                .where(concertEntity.hallCode.eq(hallCode), ltConcertCode(lastConcertCode))
                .orderBy(concertEntity.concertCode.desc())
                .limit(pageSize)
                .fetch();
    }

    private List<ConcertThumbnail> fetchConcerts(
            final BooleanExpression searchCondition,
            final BooleanExpression cursorCondition,
            final OrderSpecifier<?> primarySort,
            final int fetchSize) {
        return queryFactory
                .select(Projections.constructor(
                        ConcertThumbnail.class,
                        concertEntity.posterUrl,
                        concertEntity.title,
                        concertHallEntity.name,
                        concertEntity.startDate,
                        concertEntity.endDate,
                        concertEntity.concertCode))
                .from(concertEntity)
                .leftJoin(concertHallEntity)
                .on(concertEntity.hallCode.eq(concertHallEntity.hallCode))
                .where(searchCondition, cursorCondition)
                .orderBy(primarySort, concertEntity.concertCode.desc())
                .limit(fetchSize)
                .fetch();
    }

    private SliceResponse<ConcertThumbnail, String> buildResponse(
            final List<ConcertThumbnail> results, final int pageSize) {
        String nextCursorCode =
                results.size() > pageSize ? results.get(pageSize - 1).concertCode() : null;
        List<ConcertThumbnail> page = results.stream().limit(pageSize).toList();
        return new SliceResponse<>(page, nextCursorCode);
    }

    private BooleanExpression titleMatchCondition(final String query) {
        if (!StringUtils.hasText(query)) {
            return null;
        }
        NumberTemplate<Double> sim =
                Expressions.numberTemplate(Double.class, "similarity({0}, {1})", concertEntity.title, query);
        BooleanExpression ilike =
                Expressions.booleanTemplate("({0} ilike {1})", concertEntity.title, "%" + query + "%");
        return sim.gt(SIMILARITY_THRESHOLD).or(ilike);
    }

    private BooleanExpression cursorCondition(final String cursorCode) {
        return cursorCode != null ? concertEntity.concertCode.lt(cursorCode) : null;
    }

    private OrderSpecifier<?> similarityOrder(final String query) {
        if (!StringUtils.hasText(query)) {
            return concertEntity.concertCode.desc();
        }
        return Expressions.numberTemplate(Double.class, "similarity({0}, {1})", concertEntity.title, query)
                .desc();
    }

    private OrderSpecifier<?> mainSortOrder(final SortDirection sortDirection) {
        return switch (sortDirection) {
            case DATE -> concertEntity.startDate.desc();
            case VIEWS -> concertEntity.concertCode.desc();
        };
    }

    private BooleanExpression ltConcertCode(final String lastConcertCode) {
        return lastConcertCode != null ? concertEntity.concertCode.lt(lastConcertCode) : null;
    }
}
