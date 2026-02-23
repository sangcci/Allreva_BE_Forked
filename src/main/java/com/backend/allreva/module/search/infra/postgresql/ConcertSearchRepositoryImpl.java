package com.backend.allreva.module.search.infra.postgresql;

import com.backend.allreva.module.concert.concert.domain.QConcert;
import com.backend.allreva.module.concert.place.domain.QConcertHall;
import com.backend.allreva.module.search.application.dto.ConcertMainResponse;
import com.backend.allreva.module.search.application.dto.ConcertSearchListResponse;
import com.backend.allreva.module.search.application.dto.ConcertThumbnail;
import com.backend.allreva.module.search.domain.ConcertSearchRepository;
import com.backend.allreva.module.search.domain.SortDirection;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

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
                .select(Projections.constructor(ConcertThumbnail.class,
                        concert.poster.url,
                        concert.concertInfo.title,
                        hall.name,
                        concert.concertInfo.dateInfo.startDate,
                        concert.concertInfo.dateInfo.endDate,
                        concert.id))
                .from(concert)
                .leftJoin(hall).on(concert.code.hallCode.eq(hall.id))
                .where(titleMatchCondition(title))
                .orderBy(similarityOrder(title), concert.id.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public ConcertSearchListResponse searchByTitle(
            final String query, final Long cursorId, final int pageSize) {
        List<ConcertThumbnail> results = fetchConcerts(
                titleMatchCondition(query),
                cursorCondition(cursorId),
                similarityOrder(query),
                pageSize + 1);

        return buildResponse(results, pageSize);
    }

    @Override
    public ConcertSearchListResponse searchByTitleAll(
            final String query, final Long cursorId, final int pageSize) {
        return searchByTitle(query, cursorId, pageSize);
    }

    @Override
    public ConcertMainResponse searchMain(
            final String address, final Long cursorId,
            final int pageSize, final SortDirection sortDirection) {
        BooleanExpression addressCondition = StringUtils.hasText(address)
                ? Expressions.booleanTemplate("({0} ilike {1})",
                        hall.location.address, "%" + address + "%")
                : null;

        List<ConcertThumbnail> results = fetchConcerts(
                addressCondition,
                cursorCondition(cursorId),
                mainSortOrder(sortDirection),
                pageSize + 1);

        Long nextCursorId = results.size() > pageSize
                ? results.get(pageSize - 1).id()
                : null;
        List<ConcertThumbnail> page = results.stream().limit(pageSize).toList();
        return ConcertMainResponse.from(page, nextCursorId);
    }

    private List<ConcertThumbnail> fetchConcerts(
            BooleanExpression searchCondition,
            BooleanExpression cursorCondition,
            OrderSpecifier<?> primarySort,
            int fetchSize) {
        return queryFactory
                .select(Projections.constructor(ConcertThumbnail.class,
                        concert.poster.url,
                        concert.concertInfo.title,
                        hall.name,
                        concert.concertInfo.dateInfo.startDate,
                        concert.concertInfo.dateInfo.endDate,
                        concert.id))
                .from(concert)
                .leftJoin(hall).on(concert.code.hallCode.eq(hall.id))
                .where(searchCondition, cursorCondition)
                .orderBy(primarySort, concert.id.desc())
                .limit(fetchSize)
                .fetch();
    }

    private ConcertSearchListResponse buildResponse(List<ConcertThumbnail> results, int pageSize) {
        Long nextCursorId = results.size() > pageSize
                ? results.get(pageSize - 1).id()
                : null;
        List<ConcertThumbnail> page = results.stream().limit(pageSize).toList();
        return ConcertSearchListResponse.from(page, nextCursorId);
    }

    private BooleanExpression titleMatchCondition(final String query) {
        if (!StringUtils.hasText(query)) return null;
        NumberTemplate<Double> sim = Expressions.numberTemplate(Double.class,
                "similarity({0}, {1})", concert.concertInfo.title, query);
        BooleanExpression ilike = Expressions.booleanTemplate("({0} ilike {1})",
                concert.concertInfo.title, "%" + query + "%");
        return sim.gt(SIMILARITY_THRESHOLD).or(ilike);
    }

    private BooleanExpression cursorCondition(final Long cursorId) {
        return cursorId != null ? concert.id.lt(cursorId) : null;
    }

    private OrderSpecifier<?> similarityOrder(final String query) {
        if (!StringUtils.hasText(query)) return concert.id.desc();
        return Expressions.numberTemplate(Double.class,
                "similarity({0}, {1})", concert.concertInfo.title, query).desc();
    }

    private OrderSpecifier<?> mainSortOrder(final SortDirection sortDirection) {
        return switch (sortDirection) {
            case DATE -> concert.concertInfo.dateInfo.startDate.desc();
            case VIEWS -> concert.viewCount.desc();
            default -> concert.id.desc();
        };
    }
}
