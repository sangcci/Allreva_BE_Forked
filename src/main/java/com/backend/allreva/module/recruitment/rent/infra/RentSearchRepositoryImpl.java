package com.backend.allreva.module.recruitment.rent.infra;

import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentThumbnail;
import com.backend.allreva.module.recruitment.rent.application.port.RentSearchRepository;
import com.backend.allreva.module.recruitment.rent.domain.QRent;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class RentSearchRepositoryImpl implements RentSearchRepository {

    private static final double SIMILARITY_THRESHOLD = 0.1;
    private final JPAQueryFactory queryFactory;
    private final QRent rent = QRent.rent;

    @Override
    public List<RentThumbnail> findThumbnailsByTitle(final String title, final int limit) {
        return fetchRents(titleMatchCondition(title), null, limit);
    }

    @Override
    public SliceResponse<RentThumbnail, Long> findAllByTitle(
            final String query, final Long cursorId, final int pageSize) {
        BooleanExpression notExpired = rent.endDate.goe(LocalDate.now());
        BooleanExpression titleMatch = titleMatchCondition(query);
        BooleanExpression cursor = cursorId != null ? rent.id.lt(cursorId) : null;

        List<RentThumbnail> results =
                fetchRents(titleMatch != null ? titleMatch.and(notExpired) : notExpired, cursor, pageSize + 1);

        Long nextCursorId =
                results.size() > pageSize ? results.get(pageSize - 1).id() : null;
        return new SliceResponse<>(results.stream().limit(pageSize).toList(), nextCursorId);
    }

    private List<RentThumbnail> fetchRents(BooleanExpression condition, BooleanExpression cursor, int fetchSize) {
        return queryFactory
                .select(Projections.constructor(
                        RentThumbnail.class, rent.id, rent.title, rent.region, rent.image.url, rent.endDate))
                .from(rent)
                .where(condition, cursor)
                .orderBy(rent.id.desc())
                .limit(fetchSize)
                .fetch();
    }

    private BooleanExpression titleMatchCondition(final String query) {
        if (!StringUtils.hasText(query)) return null;
        NumberTemplate<Double> sim =
                Expressions.numberTemplate(Double.class, "similarity({0}, {1})", rent.title, query);
        BooleanExpression ilike = Expressions.booleanTemplate("({0} ilike {1})", rent.title, "%" + query + "%");
        return sim.gt(SIMILARITY_THRESHOLD).or(ilike);
    }
}
