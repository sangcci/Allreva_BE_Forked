package com.backend.allreva.module.recruitment.survey.infra;

import static com.backend.allreva.module.recruitment.survey.domain.QSurvey.survey;
import static com.backend.allreva.survey_join.command.domain.QSurveyJoin.surveyJoin;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.util.DateHolder;
import com.backend.allreva.module.recruitment.survey.application.dto.SortType;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveyBoardingDateResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveyDetailResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveyMainResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveySummaryResponse;
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
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SurveyRepositoryImpl implements SurveyRepository {

    private final SurveyJpaRepository surveyJpaRepository;
    private final JPAQueryFactory queryFactory;
    private final DateHolder dateHolder;

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
        Survey found = surveyJpaRepository.findById(surveyId)
                .orElseThrow(() -> new CustomException(SurveyErrorCode.SURVEY_NOT_FOUND));

        List<SurveyBoardingDateResponse> boardingDateResponses = found.getBoardingDates().stream()
                .map(date -> {
                    Integer count = queryFactory
                            .select(surveyJoin.passengerNum.sumAggregate())
                            .from(surveyJoin)
                            .where(surveyJoin.surveyId.eq(surveyId)
                                    .and(surveyJoin.boardingDate.eq(date))
                                    .and(surveyJoin.deletedAt.isNull()))
                            .fetchOne();
                    return new SurveyBoardingDateResponse(date, count != null ? count : 0);
                })
                .collect(Collectors.toList());

        return new SurveyDetailResponse(
                found.getId(),
                found.getTitle(),
                boardingDateResponses,
                found.getInformation(),
                found.isClosed());
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
                .leftJoin(surveyJoin).on(survey.id.eq(surveyJoin.surveyId))
                .where(survey.endDate.goe(dateHolder.getDate()),
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
                .leftJoin(surveyJoin).on(survey.id.eq(surveyJoin.surveyId))
                .where(survey.endDate.goe(LocalDate.now()))
                .groupBy(survey.id)
                .orderBy(survey.endDate.asc())
                .limit(3)
                .fetch();
    }

    @Override
    public Optional<SurveyMainResponse> findSurveyWithParticipationCount(final Long surveyId) {
        return Optional.ofNullable(queryFactory
                .select(Projections.constructor(SurveyMainResponse.class,
                        survey.id,
                        survey.title,
                        survey.region,
                        Expressions.as(
                                JPAExpressions
                                        .select(surveyJoin.passengerNum.sumAggregate().coalesce(0))
                                        .from(surveyJoin)
                                        .where(surveyJoin.surveyId.eq(survey.id)
                                                .and(surveyJoin.deletedAt.isNull())),
                                "participationCount"),
                        survey.endDate))
                .from(survey)
                .where(survey.id.eq(surveyId)
                        .and(survey.deletedAt.isNull()))
                .fetchOne());
    }

    private ConstructorExpression<SurveySummaryResponse> surveySummaryProjections() {
        return Projections.constructor(SurveySummaryResponse.class,
                survey.id,
                survey.title,
                survey.region,
                surveyJoin.passengerNum.sumAggregate(),
                survey.endDate);
    }

    private static BooleanExpression getRegionCondition(final Region region) {
        return region == null ? null : survey.region.eq(region);
    }

    private BooleanExpression getPagingCondition(
            final SortType sortType,
            final Long lastId,
            final LocalDate lastEndDate) {
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
                return new OrderSpecifier[]{
                        survey.endDate.asc(),
                        survey.id.asc()
                };
            }
            case OLDEST -> {
                return new OrderSpecifier[]{
                        survey.id.asc()
                };
            }
            default -> {
                return new OrderSpecifier[]{
                        survey.id.desc()
                };
            }
        }
    }
}
