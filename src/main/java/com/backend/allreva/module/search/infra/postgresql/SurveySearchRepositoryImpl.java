package com.backend.allreva.module.search.infra.postgresql;

import com.backend.allreva.module.search.application.dto.SurveySearchListResponse;
import com.backend.allreva.module.search.application.dto.SurveyThumbnail;
import com.backend.allreva.module.search.application.port.SurveySearchRepository;
import com.backend.allreva.module.recruitment.survey.domain.QSurvey;
import com.backend.allreva.module.recruitment.survey.domain.participant.QSurveyParticipant;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SurveySearchRepositoryImpl implements SurveySearchRepository {

    private static final double SIMILARITY_THRESHOLD = 0.1;
    private final JPAQueryFactory queryFactory;
    private final QSurvey survey = QSurvey.survey;
    private final QSurveyParticipant surveyParticipant = QSurveyParticipant.surveyParticipant;

    @Override
    public List<SurveyThumbnail> findThumbnailsByTitle(final String title, final int limit) {
        return fetchSurveys(titleMatchCondition(title), null, limit);
    }

    @Override
    public SurveySearchListResponse searchByTitle(
            final String query, final Long cursorId, final int pageSize) {
        BooleanExpression notExpired = survey.endDate.goe(LocalDate.now());
        BooleanExpression titleMatch = titleMatchCondition(query);
        BooleanExpression cursor = cursorId != null ? survey.id.lt(cursorId) : null;

        List<SurveyThumbnail> results = fetchSurveys(
                titleMatch != null ? titleMatch.and(notExpired) : notExpired,
                cursor,
                pageSize + 1);

        Long nextCursorId = results.size() > pageSize
                ? results.get(pageSize - 1).id()
                : null;
        return SurveySearchListResponse.from(results.stream().limit(pageSize).toList(), nextCursorId);
    }

    private List<SurveyThumbnail> fetchSurveys(
            BooleanExpression condition, BooleanExpression cursor, int fetchSize) {
        return queryFactory
                .select(Projections.constructor(SurveyThumbnail.class,
                        survey.id,
                        survey.title,
                        survey.region.stringValue(),
                        surveyParticipant.id.count().intValue(),
                        survey.endDate))
                .from(survey)
                .leftJoin(surveyParticipant).on(surveyParticipant.surveyId.eq(survey.id).and(surveyParticipant.deletedAt.isNull()))
                .where(condition, cursor)
                .groupBy(survey.id)
                .orderBy(similarityOrder(null), survey.id.desc())
                .limit(fetchSize)
                .fetch();
    }

    private BooleanExpression titleMatchCondition(final String query) {
        if (!StringUtils.hasText(query)) return null;
        NumberTemplate<Double> sim = Expressions.numberTemplate(Double.class,
                "similarity({0}, {1})", survey.title, query);
        BooleanExpression ilike = Expressions.booleanTemplate("({0} ilike {1})",
                survey.title, "%" + query + "%");
        return sim.gt(SIMILARITY_THRESHOLD).or(ilike);
    }

    private com.querydsl.core.types.OrderSpecifier<?> similarityOrder(final String query) {
        if (!StringUtils.hasText(query)) return survey.id.desc();
        return Expressions.numberTemplate(Double.class,
                "similarity({0}, {1})", survey.title, query).desc();
    }
}
