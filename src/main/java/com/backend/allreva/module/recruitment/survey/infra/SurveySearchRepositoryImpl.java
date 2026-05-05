package com.backend.allreva.module.recruitment.survey.infra;

import static com.backend.allreva.module.recruitment.survey.domain.QSurvey.survey;
import static com.backend.allreva.module.recruitment.survey.domain.participant.QSurveyParticipant.surveyParticipant;

import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveyThumbnail;
import com.backend.allreva.module.recruitment.survey.application.port.SurveySearchRepository;
import com.querydsl.core.types.OrderSpecifier;
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
public class SurveySearchRepositoryImpl implements SurveySearchRepository {

    private static final double SIMILARITY_THRESHOLD = 0.1;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<SurveyThumbnail> findThumbnailsByTitle(final String title, final int limit) {
        return fetchSurveys(titleMatchCondition(title), null, limit);
    }

    @Override
    public SliceResponse<SurveyThumbnail, Long> findAllByTitle(
            final String query, final Long cursorId, final int pageSize) {
        BooleanExpression notExpired = survey.endDate.goe(LocalDate.now());
        BooleanExpression titleMatch = titleMatchCondition(query);
        BooleanExpression cursor = cursorId != null ? survey.id.lt(cursorId) : null;

        List<SurveyThumbnail> results =
                fetchSurveys(titleMatch != null ? titleMatch.and(notExpired) : notExpired, cursor, pageSize + 1);

        Long nextCursorId =
                results.size() > pageSize ? results.get(pageSize - 1).id() : null;
        return new SliceResponse<>(results.stream().limit(pageSize).toList(), nextCursorId);
    }

    private List<SurveyThumbnail> fetchSurveys(BooleanExpression condition, BooleanExpression cursor, int fetchSize) {
        return queryFactory
                .select(Projections.constructor(
                        SurveyThumbnail.class,
                        survey.id,
                        survey.title,
                        survey.region.stringValue(),
                        surveyParticipant.id.count().intValue(),
                        survey.endDate))
                .from(survey)
                .leftJoin(surveyParticipant)
                .on(surveyParticipant.surveyId.eq(survey.id).and(surveyParticipant.deletedAt.isNull()))
                .where(condition, cursor)
                .groupBy(survey.id)
                .orderBy(similarityOrder(null), survey.id.desc())
                .limit(fetchSize)
                .fetch();
    }

    private BooleanExpression titleMatchCondition(final String query) {
        if (!StringUtils.hasText(query)) return null;
        NumberTemplate<Double> sim =
                Expressions.numberTemplate(Double.class, "similarity({0}, {1})", survey.title, query);
        BooleanExpression ilike = Expressions.booleanTemplate("({0} ilike {1})", survey.title, "%" + query + "%");
        return sim.gt(SIMILARITY_THRESHOLD).or(ilike);
    }

    private OrderSpecifier<?> similarityOrder(final String query) {
        if (!StringUtils.hasText(query)) return survey.id.desc();
        return Expressions.numberTemplate(Double.class, "similarity({0}, {1})", survey.title, query)
                .desc();
    }
}
