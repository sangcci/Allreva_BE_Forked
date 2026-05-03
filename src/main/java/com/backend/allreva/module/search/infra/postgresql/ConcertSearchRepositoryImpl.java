package com.backend.allreva.module.search.infra.postgresql;

import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.module.concert.concert.domain.QConcert;
import com.backend.allreva.module.concert.place.domain.QConcertHall;
import com.backend.allreva.module.search.application.dto.ConcertThumbnail;
import com.backend.allreva.module.search.application.dto.SortDirection;
import com.backend.allreva.module.search.application.port.ConcertSearchRepository;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class ConcertSearchRepositoryImpl implements ConcertSearchRepository {

    private static final double SIMILARITY_THRESHOLD = 0.1;
    private final JPAQueryFactory queryFactory;
    private final QConcert concert = QConcert.concert;
    private final QConcertHall hall = QConcertHall.concertHall;

    @Override
    public List<ConcertThumbnail> findThumbnailsByTitle(final String title, final int limit) {
        return queryFactory
                .select(Projections.constructor(
                        ConcertThumbnail.class,
                        concert.poster.url,
                        concert.concertInfo.title,
                        hall.name,
                        concert.concertInfo.dateInfo.startDate,
                        concert.concertInfo.dateInfo.endDate,
                        concert.concertCode))
                .from(concert)
                .leftJoin(hall)
                .on(concert.hallCode.eq(hall.hallCode))
                .where(titleMatchCondition(title))
                .orderBy(similarityOrder(title), concert.concertCode.desc())
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
                ? Expressions.booleanTemplate("({0} ilike {1})", hall.location.address, "%" + address + "%")
                : null;

        List<ConcertThumbnail> results = fetchConcerts(
                addressCondition, cursorCondition(cursorCode), mainSortOrder(sortDirection), pageSize + 1);

        String nextCursorCode =
                results.size() > pageSize ? results.get(pageSize - 1).concertCode() : null;
        List<ConcertThumbnail> page = results.stream().limit(pageSize).toList();
        return new SliceResponse<>(page, nextCursorCode);
    }

    private List<ConcertThumbnail> fetchConcerts(
            BooleanExpression searchCondition,
            BooleanExpression cursorCondition,
            OrderSpecifier<?> primarySort,
            int fetchSize) {
        return queryFactory
                .select(Projections.constructor(
                        ConcertThumbnail.class,
                        concert.poster.url,
                        concert.concertInfo.title,
                        hall.name,
                        concert.concertInfo.dateInfo.startDate,
                        concert.concertInfo.dateInfo.endDate,
                        concert.concertCode))
                .from(concert)
                .leftJoin(hall)
                .on(concert.hallCode.eq(hall.hallCode))
                .where(searchCondition, cursorCondition)
                .orderBy(primarySort, concert.concertCode.desc())
                .limit(fetchSize)
                .fetch();
    }

    private SliceResponse<ConcertThumbnail, String> buildResponse(List<ConcertThumbnail> results, int pageSize) {
        String nextCursorCode =
                results.size() > pageSize ? results.get(pageSize - 1).concertCode() : null;
        List<ConcertThumbnail> page = results.stream().limit(pageSize).toList();
        return new SliceResponse<>(page, nextCursorCode);
    }

    private BooleanExpression titleMatchCondition(final String query) {
        if (!StringUtils.hasText(query)) return null;
        NumberTemplate<Double> sim =
                Expressions.numberTemplate(Double.class, "similarity({0}, {1})", concert.concertInfo.title, query);
        BooleanExpression ilike =
                Expressions.booleanTemplate("({0} ilike {1})", concert.concertInfo.title, "%" + query + "%");
        return sim.gt(SIMILARITY_THRESHOLD).or(ilike);
    }

    private BooleanExpression cursorCondition(final String cursorCode) {
        return cursorCode != null ? concert.concertCode.lt(cursorCode) : null;
    }

    private OrderSpecifier<?> similarityOrder(final String query) {
        if (!StringUtils.hasText(query)) return concert.concertCode.desc();
        return Expressions.numberTemplate(Double.class, "similarity({0}, {1})", concert.concertInfo.title, query)
                .desc();
    }

    private OrderSpecifier<?> mainSortOrder(final SortDirection sortDirection) {
        return switch (sortDirection) {
            case DATE -> concert.concertInfo.dateInfo.startDate.desc();
            case VIEWS -> concert.concertCode.desc();
            default -> concert.concertCode.desc();
        };
    }
}
