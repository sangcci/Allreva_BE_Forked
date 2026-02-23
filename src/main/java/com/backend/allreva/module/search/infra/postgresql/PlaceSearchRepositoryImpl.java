package com.backend.allreva.module.search.infra.postgresql;

import com.backend.allreva.module.concert.place.application.dto.ConcertHallMainResponse;
import com.backend.allreva.module.concert.place.application.dto.ConcertHallThumbnail;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.QConcertHall;
import com.backend.allreva.module.search.domain.PlaceSearchRepository;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlaceSearchRepositoryImpl implements PlaceSearchRepository {

    private final JPAQueryFactory queryFactory;
    private final QConcertHall hall = QConcertHall.concertHall;

    @Override
    public ConcertHallMainResponse searchMain(
            final String address, final int minSeatSize,
            final String cursorId, final int pageSize) {

        List<ConcertHall> results = queryFactory
                .selectFrom(hall)
                .where(
                        addressCondition(address),
                        seatScaleCondition(minSeatSize),
                        cursorCondition(cursorId)
                )
                .orderBy(primarySortOrder(address, minSeatSize), hall.id.asc())
                .limit(pageSize + 1L)
                .fetch();

        String nextCursorId = results.size() > pageSize
                ? results.get(pageSize - 1).getId()
                : null;

        List<ConcertHallThumbnail> thumbnails = results.stream()
                .limit(pageSize)
                .map(ConcertHallThumbnail::from)
                .toList();

        return ConcertHallMainResponse.from(thumbnails, nextCursorId);
    }

    private BooleanExpression addressCondition(final String address) {
        if (!StringUtils.hasText(address)) return null;
        return Expressions.booleanTemplate("({0} ilike {1})",
                hall.location.address, "%" + address + "%");
    }

    private BooleanExpression seatScaleCondition(final int minSeatSize) {
        return minSeatSize > 0 ? hall.seatScale.goe(minSeatSize) : null;
    }

    private BooleanExpression cursorCondition(final String cursorId) {
        return cursorId != null ? hall.id.gt(cursorId) : null;
    }

    private OrderSpecifier<?> primarySortOrder(final String address, final int minSeatSize) {
        if (minSeatSize > 0) return hall.seatScale.desc();
        if (StringUtils.hasText(address)) {
            return Expressions.numberTemplate(Double.class,
                    "similarity({0}, {1})", hall.location.address, address).desc();
        }
        return hall.id.asc();
    }
}
