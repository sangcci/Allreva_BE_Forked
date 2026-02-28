package com.backend.allreva.module.recruitment.survey.infra;

import static com.backend.allreva.module.recruitment.survey.domain.QSurvey.survey;
import static com.backend.allreva.module.recruitment.survey.domain.participant.QSurveyParticipant.surveyParticipant;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.recruitment.survey.application.dto.*;
import com.backend.allreva.module.recruitment.survey.domain.Survey;
import com.backend.allreva.module.recruitment.survey.domain.SurveyRepository;
import com.backend.allreva.module.recruitment.survey.domain.value.Region;
import com.backend.allreva.module.recruitment.survey.exception.SurveyErrorCode;
import com.backend.allreva.module.recruitment.survey.infra.jpa.SurveyJpaRepository;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
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

@Repository
@RequiredArgsConstructor
public class SurveyRepositoryImpl implements SurveyRepository {

    private final SurveyJpaRepository surveyJpaRepository;
    private final JPAQueryFactory queryFactory;
    private final Clock clock;

    @Override
    public Survey save(final Survey surveyEntity) {
        return surveyJpaRepository.save(surveyEntity);
    }

    @Override
    public Optional<Survey> findById(final Long id) {
        return surveyJpaRepository.findById(id);
    }

    @Override
    public void delete(final Survey surveyEntity) {
        surveyJpaRepository.delete(surveyEntity);
    }

    @Override
    public void closeSurveys(final LocalDate today) {
        surveyJpaRepository.closeSurveys(today);
    }

    @Override
    public SurveyDetailResponse findSurveyDetail(final Long surveyId) {
        Survey found = surveyJpaRepository
                .findById(surveyId)
                .orElseThrow(() -> new CustomException(SurveyErrorCode.SURVEY_NOT_FOUND));

        NumberExpression<Integer> passengerSum =
                surveyParticipant.passengerNum.sumAggregate().intValue();
        Map<LocalDate, Integer> countByDate = queryFactory
                .select(surveyParticipant.boardingDate, passengerSum)
                .from(surveyParticipant)
                .where(surveyParticipant.surveyId.eq(surveyId).and(surveyParticipant.deletedAt.isNull()))
                .groupBy(surveyParticipant.boardingDate)
                .fetch()
                .stream()
                .collect(Collectors.toMap(tuple -> tuple.get(surveyParticipant.boardingDate), tuple -> {
                    Integer count = tuple.get(passengerSum);
                    return count != null ? count : 0;
                }));

        List<SurveyBoardingDateResponse> boardingDateResponses = found.getBoardingDates().stream()
                .map(date -> new SurveyBoardingDateResponse(date, countByDate.getOrDefault(date, 0)))
                .toList();

        return new SurveyDetailResponse(
                found.getId(), found.getTitle(), boardingDateResponses, found.getInformation(), found.isClosed());
    }

    @Override
    public List<SurveySummaryResponse> findSurveyList(
            final Region region,
            final SortType sortType,
            final Long lastId,
            final LocalDate lastEndDate,
            final int pageSize) {
        return queryFactory
                .select(surveySummaryProjections())
                .from(survey)
                .leftJoin(surveyParticipant)
                .on(surveyParticipant.surveyId.eq(survey.id).and(surveyParticipant.deletedAt.isNull()))
                .where(
                        survey.deletedAt.isNull(),
                        survey.endDate.goe(LocalDate.now(clock)),
                        getRegionCondition(region),
                        getPagingCondition(sortType, lastId, lastEndDate))
                .groupBy(survey.id)
                .orderBy(orderSpecifiers(sortType))
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<SurveySummaryResponse> findSurveyMainList() {
        return queryFactory
                .select(surveySummaryProjections())
                .from(survey)
                .leftJoin(surveyParticipant)
                .on(surveyParticipant.surveyId.eq(survey.id).and(surveyParticipant.deletedAt.isNull()))
                .where(survey.deletedAt.isNull(), survey.endDate.goe(LocalDate.now(clock)))
                .groupBy(survey.id)
                .orderBy(survey.endDate.asc())
                .limit(3)
                .fetch();
    }

    @Override
    public Optional<SurveyMainResponse> findSurveyWithParticipationCount(final Long surveyId) {
        return Optional.ofNullable(queryFactory
                .select(Projections.constructor(
                        SurveyMainResponse.class,
                        survey.id,
                        survey.title,
                        survey.region,
                        Expressions.as(
                                JPAExpressions.select(surveyParticipant
                                                .passengerNum
                                                .sumAggregate()
                                                .coalesce(0))
                                        .from(surveyParticipant)
                                        .where(surveyParticipant
                                                .surveyId
                                                .eq(survey.id)
                                                .and(surveyParticipant.deletedAt.isNull())),
                                "participationCount"),
                        survey.endDate))
                .from(survey)
                .where(survey.id.eq(surveyId).and(survey.deletedAt.isNull()))
                .fetchOne());
    }

    private ConstructorExpression<SurveySummaryResponse> surveySummaryProjections() {
        return Projections.constructor(
                SurveySummaryResponse.class,
                survey.id,
                survey.title,
                survey.region,
                surveyParticipant.passengerNum.sumAggregate(),
                survey.endDate);
    }

    private static BooleanExpression getRegionCondition(final Region region) {
        return region == null ? null : survey.region.eq(region);
    }

    private BooleanExpression getPagingCondition(
            final SortType sortType, final Long lastId, final LocalDate lastEndDate) {
        if (lastId == null && lastEndDate == null) {
            return null;
        }

        switch (sortType) {
            case CLOSING -> {
                return (survey.endDate.gt(lastEndDate))
                        .or(survey.endDate.eq(lastEndDate).and(survey.id.gt(lastId)));
            }
            case OLDEST -> {
                return survey.id.gt(lastId);
            }
            default -> {
                return survey.id.lt(lastId);
            }
        }
    }

    private OrderSpecifier<?>[] orderSpecifiers(final SortType sortType) {
        switch (sortType) {
            case CLOSING -> {
                return new OrderSpecifier[] {survey.endDate.asc(), survey.id.asc()};
            }
            case OLDEST -> {
                return new OrderSpecifier[] {survey.id.asc()};
            }
            default -> {
                return new OrderSpecifier[] {survey.id.desc()};
            }
        }
    }
}
