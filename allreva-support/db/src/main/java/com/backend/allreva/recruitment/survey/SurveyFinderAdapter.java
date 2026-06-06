package com.backend.allreva.recruitment.survey;

import static com.backend.allreva.recruitment.survey.QSurveyEntity.surveyEntity;
import static com.backend.allreva.recruitment.survey.QSurveyParticipantEntity.surveyParticipantEntity;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.recruitment.survey.domain.Region;
import com.backend.allreva.recruitment.survey.domain.SortType;
import com.backend.allreva.recruitment.survey.domain.SurveyBoardingDate;
import com.backend.allreva.recruitment.survey.domain.SurveyErrorCode;
import com.backend.allreva.recruitment.survey.query.implementation.SurveyFinderPort;
import com.backend.allreva.recruitment.survey.query.model.SurveyDetail;
import com.backend.allreva.recruitment.survey.query.model.SurveyMain;
import com.backend.allreva.recruitment.survey.query.model.SurveySummary;
import com.backend.allreva.recruitment.survey.query.model.SurveyThumbnail;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class SurveyFinderAdapter implements SurveyFinderPort {

    private static final double SIMILARITY_THRESHOLD = 0.1;

    private final JPAQueryFactory queryFactory;
    private final Clock clock;

    @Override
    public List<SurveyThumbnail> findThumbnailsByTitle(final String title, final int limit) {
        return fetchSurveys(titleMatchCondition(title), null, limit);
    }

    @Override
    public SliceResponse<SurveyThumbnail, Long> findAllByTitle(
            final String query, final Long cursorId, final int pageSize) {
        BooleanExpression notExpired = surveyEntity.endDate.goe(LocalDate.now(clock));
        BooleanExpression titleMatch = titleMatchCondition(query);
        BooleanExpression cursor = cursorId != null ? surveyEntity.id.lt(cursorId) : null;

        List<SurveyThumbnail> results =
                fetchSurveys(titleMatch != null ? titleMatch.and(notExpired) : notExpired, cursor, pageSize + 1);
        Long nextCursorId =
                results.size() > pageSize ? results.get(pageSize - 1).id() : null;
        return new SliceResponse<>(results.stream().limit(pageSize).toList(), nextCursorId);
    }

    @Override
    public SurveyDetail findSurveyDetail(final Long surveyId) {
        SurveyEntity found = Optional.ofNullable(queryFactory
                        .selectFrom(surveyEntity)
                        .where(surveyEntity.id.eq(surveyId), surveyEntity.deletedAt.isNull())
                        .fetchOne())
                .orElseThrow(() -> new CustomException(SurveyErrorCode.SURVEY_NOT_FOUND));

        NumberExpression<Integer> passengerSum =
                surveyParticipantEntity.passengerNum.sumAggregate().intValue();
        Map<LocalDate, Integer> countByDate = queryFactory
                .select(surveyParticipantEntity.boardingDate, passengerSum)
                .from(surveyParticipantEntity)
                .where(surveyParticipantEntity.surveyId.eq(surveyId).and(surveyParticipantEntity.deletedAt.isNull()))
                .groupBy(surveyParticipantEntity.boardingDate)
                .fetch()
                .stream()
                .collect(Collectors.toMap(tuple -> tuple.get(surveyParticipantEntity.boardingDate), tuple -> {
                    Integer count = tuple.get(passengerSum);
                    return count != null ? count : 0;
                }));

        List<SurveyBoardingDate> boardingDates = found.getBoardingDates().stream()
                .map(date -> new SurveyBoardingDate(date, countByDate.getOrDefault(date, 0)))
                .toList();

        return new SurveyDetail(
                found.getId(), found.getTitle(), boardingDates, found.getInformation(), found.isClosed());
    }

    @Override
    public List<SurveySummary> findSurveyList(
            final Region region,
            final SortType sortType,
            final Long lastId,
            final LocalDate lastEndDate,
            final int pageSize) {
        return queryFactory
                .select(surveySummaryProjection())
                .from(surveyEntity)
                .leftJoin(surveyParticipantEntity)
                .on(surveyParticipantEntity
                        .surveyId
                        .eq(surveyEntity.id)
                        .and(surveyParticipantEntity.deletedAt.isNull()))
                .where(
                        surveyEntity.deletedAt.isNull(),
                        surveyEntity.endDate.goe(LocalDate.now(clock)),
                        regionCondition(region),
                        pagingCondition(sortType, lastId, lastEndDate))
                .groupBy(surveyEntity.id)
                .orderBy(orderSpecifiers(sortType))
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<SurveySummary> findSurveyMainList() {
        return queryFactory
                .select(surveySummaryProjection())
                .from(surveyEntity)
                .leftJoin(surveyParticipantEntity)
                .on(surveyParticipantEntity
                        .surveyId
                        .eq(surveyEntity.id)
                        .and(surveyParticipantEntity.deletedAt.isNull()))
                .where(surveyEntity.deletedAt.isNull(), surveyEntity.endDate.goe(LocalDate.now(clock)))
                .groupBy(surveyEntity.id)
                .orderBy(surveyEntity.endDate.asc())
                .limit(3)
                .fetch();
    }

    @Override
    public Optional<SurveyMain> findSurveyWithParticipationCount(final Long surveyId) {
        return Optional.ofNullable(queryFactory
                .select(Projections.constructor(
                        SurveyMain.class,
                        surveyEntity.id,
                        surveyEntity.title,
                        surveyEntity.region,
                        Expressions.as(
                                JPAExpressions.select(surveyParticipantEntity
                                                .passengerNum
                                                .sumAggregate()
                                                .coalesce(0))
                                        .from(surveyParticipantEntity)
                                        .where(surveyParticipantEntity
                                                .surveyId
                                                .eq(surveyEntity.id)
                                                .and(surveyParticipantEntity.deletedAt.isNull())),
                                "participationCount"),
                        surveyEntity.endDate))
                .from(surveyEntity)
                .where(surveyEntity.id.eq(surveyId).and(surveyEntity.deletedAt.isNull()))
                .fetchOne());
    }

    private List<SurveyThumbnail> fetchSurveys(
            final BooleanExpression condition, final BooleanExpression cursor, final int fetchSize) {
        return queryFactory
                .select(Projections.constructor(
                        SurveyThumbnail.class,
                        surveyEntity.id,
                        surveyEntity.title,
                        surveyEntity.region.stringValue(),
                        surveyParticipantEntity.id.count().intValue(),
                        surveyEntity.endDate))
                .from(surveyEntity)
                .leftJoin(surveyParticipantEntity)
                .on(surveyParticipantEntity
                        .surveyId
                        .eq(surveyEntity.id)
                        .and(surveyParticipantEntity.deletedAt.isNull()))
                .where(condition, cursor)
                .groupBy(surveyEntity.id)
                .orderBy(similarityOrder(null), surveyEntity.id.desc())
                .limit(fetchSize)
                .fetch();
    }

    private BooleanExpression titleMatchCondition(final String query) {
        if (!StringUtils.hasText(query)) {
            return null;
        }
        NumberTemplate<Double> sim =
                Expressions.numberTemplate(Double.class, "similarity({0}, {1})", surveyEntity.title, query);
        BooleanExpression ilike = Expressions.booleanTemplate("({0} ilike {1})", surveyEntity.title, "%" + query + "%");
        return sim.gt(SIMILARITY_THRESHOLD).or(ilike);
    }

    private OrderSpecifier<?> similarityOrder(final String query) {
        if (!StringUtils.hasText(query)) {
            return surveyEntity.id.desc();
        }
        return Expressions.numberTemplate(Double.class, "similarity({0}, {1})", surveyEntity.title, query)
                .desc();
    }

    private ConstructorExpression<SurveySummary> surveySummaryProjection() {
        return Projections.constructor(
                SurveySummary.class,
                surveyEntity.id,
                surveyEntity.title,
                surveyEntity.region,
                surveyParticipantEntity.passengerNum.sumAggregate(),
                surveyEntity.endDate);
    }

    private static BooleanExpression regionCondition(final Region region) {
        return region == null ? null : surveyEntity.region.eq(region);
    }

    private BooleanExpression pagingCondition(final SortType sortType, final Long lastId, final LocalDate lastEndDate) {
        if (lastId == null && lastEndDate == null) {
            return null;
        }

        switch (sortType) {
            case CLOSING -> {
                return surveyEntity
                        .endDate
                        .gt(lastEndDate)
                        .or(surveyEntity.endDate.eq(lastEndDate).and(surveyEntity.id.gt(lastId)));
            }
            case OLDEST -> {
                return surveyEntity.id.gt(lastId);
            }
            default -> {
                return surveyEntity.id.lt(lastId);
            }
        }
    }

    private OrderSpecifier<?>[] orderSpecifiers(final SortType sortType) {
        switch (sortType) {
            case CLOSING -> {
                return new OrderSpecifier[] {surveyEntity.endDate.asc(), surveyEntity.id.asc()};
            }
            case OLDEST -> {
                return new OrderSpecifier[] {surveyEntity.id.asc()};
            }
            default -> {
                return new OrderSpecifier[] {surveyEntity.id.desc()};
            }
        }
    }
}
