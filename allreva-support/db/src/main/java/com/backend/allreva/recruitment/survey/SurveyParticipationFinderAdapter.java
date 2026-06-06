package com.backend.allreva.recruitment.survey;

import static com.backend.allreva.recruitment.survey.QSurveyEntity.surveyEntity;
import static com.backend.allreva.recruitment.survey.QSurveyParticipantEntity.surveyParticipantEntity;

import com.backend.allreva.recruitment.survey.domain.BoardingType;
import com.backend.allreva.recruitment.survey.query.implementation.SurveyParticipationFinderPort;
import com.backend.allreva.recruitment.survey.query.model.CreatedSurvey;
import com.backend.allreva.recruitment.survey.query.model.JoinedSurvey;
import com.backend.allreva.recruitment.survey.query.model.SurveyItem;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SurveyParticipationFinderAdapter implements SurveyParticipationFinderPort {

    private static final QSurveyParticipantEntity PARTICIPANT_SUB = new QSurveyParticipantEntity("participantSub");

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CreatedSurvey> findCreatedSurveyList(
            final Long memberId, final Long lastId, final LocalDate lastBoardingDate, final int pageSize) {
        return queryFactory
                .select(createdSurveyProjection())
                .from(surveyEntity)
                .leftJoin(surveyParticipantEntity)
                .on(surveyEntity.id.eq(surveyParticipantEntity.surveyId))
                .where(surveyEntity.memberId.eq(memberId), createdSurveyPagingCondition(lastId, lastBoardingDate))
                .groupBy(surveyEntity.id, surveyParticipantEntity.boardingDate)
                .orderBy(surveyEntity.id.desc())
                .limit(pageSize)
                .fetch();
    }

    @Override
    public List<JoinedSurvey> findJoinSurveyList(final Long memberId, final Long lastId, final int pageSize) {
        return queryFactory
                .select(joinedSurveyProjection())
                .from(surveyEntity)
                .join(surveyParticipantEntity)
                .on(surveyEntity.id.eq(surveyParticipantEntity.surveyId))
                .where(surveyParticipantEntity.memberId.eq(memberId), joinSurveyPagingCondition(lastId))
                .orderBy(surveyParticipantEntity.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private ConstructorExpression<CreatedSurvey> createdSurveyProjection() {
        return Projections.constructor(
                CreatedSurvey.class,
                Projections.constructor(
                        SurveyItem.class,
                        surveyEntity.id,
                        surveyEntity.title,
                        surveyParticipantEntity.boardingDate,
                        surveyEntity.region,
                        surveyEntity.createdAt,
                        surveyEntity.endDate,
                        participationCount(),
                        surveyEntity.maxPassenger),
                passengerSumByBoardingType(BoardingType.UP),
                passengerSumByBoardingType(BoardingType.DOWN),
                passengerSumByBoardingType(BoardingType.ROUND));
    }

    private ConstructorExpression<JoinedSurvey> joinedSurveyProjection() {
        return Projections.constructor(
                JoinedSurvey.class,
                Projections.constructor(
                        SurveyItem.class,
                        surveyEntity.id,
                        surveyEntity.title,
                        surveyParticipantEntity.boardingDate,
                        surveyEntity.region,
                        surveyEntity.createdAt,
                        surveyEntity.endDate,
                        participationCount(),
                        surveyEntity.maxPassenger),
                surveyParticipantEntity.id,
                surveyParticipantEntity.createdAt,
                surveyParticipantEntity.boardingType,
                surveyParticipantEntity.passengerNum);
    }

    private BooleanExpression createdSurveyPagingCondition(final Long lastId, final LocalDate lastBoardingDate) {
        if (lastId == null && lastBoardingDate == null) {
            return null;
        }
        return surveyEntity
                .id
                .lt(lastId)
                .or(surveyEntity.id.eq(lastId).and(surveyParticipantEntity.boardingDate.gt(lastBoardingDate)));
    }

    private NumberExpression<Integer> passengerSumByBoardingType(final BoardingType boardingType) {
        return Expressions.cases()
                .when(surveyParticipantEntity.boardingType.eq(boardingType))
                .then(surveyParticipantEntity.passengerNum)
                .otherwise(0)
                .sumAggregate();
    }

    private Expression<Integer> participationCount() {
        return ExpressionUtils.as(
                JPAExpressions.select(PARTICIPANT_SUB.passengerNum.sumAggregate())
                        .from(PARTICIPANT_SUB)
                        .where(PARTICIPANT_SUB
                                .surveyId
                                .eq(surveyEntity.id)
                                .and(PARTICIPANT_SUB.boardingDate.eq(surveyParticipantEntity.boardingDate)))
                        .groupBy(PARTICIPANT_SUB.surveyId, PARTICIPANT_SUB.boardingDate),
                "participationCount");
    }

    private BooleanExpression joinSurveyPagingCondition(final Long lastId) {
        if (lastId == null) {
            return null;
        }
        return surveyEntity.id.lt(lastId);
    }
}
