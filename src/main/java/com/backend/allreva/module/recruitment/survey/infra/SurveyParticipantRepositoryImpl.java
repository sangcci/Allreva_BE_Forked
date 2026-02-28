package com.backend.allreva.module.recruitment.survey.infra;

import static com.backend.allreva.module.recruitment.survey.domain.QSurvey.survey;
import static com.backend.allreva.module.recruitment.survey.domain.participant.QSurveyParticipant.surveyParticipant;

import com.backend.allreva.module.recruitment.survey.application.dto.CreatedSurveyResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.JoinSurveyResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveyResponse;
import com.backend.allreva.module.recruitment.survey.domain.participant.QSurveyParticipant;
import com.backend.allreva.module.recruitment.survey.domain.participant.SurveyParticipant;
import com.backend.allreva.module.recruitment.survey.domain.participant.SurveyParticipantRepository;
import com.backend.allreva.module.recruitment.survey.domain.value.BoardingType;
import com.backend.allreva.module.recruitment.survey.infra.jpa.SurveyParticipantJpaRepository;
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
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SurveyParticipantRepositoryImpl implements SurveyParticipantRepository {

    private static final QSurveyParticipant participantSub = new QSurveyParticipant("participantSub");

    private final SurveyParticipantJpaRepository surveyParticipantJpaRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public SurveyParticipant save(final SurveyParticipant participant) {
        return surveyParticipantJpaRepository.save(participant);
    }

    @Override
    public Optional<SurveyParticipant> findById(final Long id) {
        return surveyParticipantJpaRepository.findById(id);
    }

    @Override
    public void delete(final SurveyParticipant participant) {
        surveyParticipantJpaRepository.delete(participant);
    }

    @Override
    public List<CreatedSurveyResponse> findCreatedSurveyList(
            final Long memberId, final Long lastId, final LocalDate lastBoardingDate, final int pageSize) {
        return queryFactory
                .select(createdSurveyProjections())
                .from(survey)
                .leftJoin(surveyParticipant)
                .on(survey.id.eq(surveyParticipant.surveyId))
                .where(survey.memberId.eq(memberId), getCreatedSurveyPagingCondition(lastId, lastBoardingDate))
                .groupBy(survey.id, surveyParticipant.boardingDate)
                .orderBy(survey.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private ConstructorExpression<CreatedSurveyResponse> createdSurveyProjections() {
        return Projections.constructor(
                CreatedSurveyResponse.class,
                Projections.constructor(
                        SurveyResponse.class,
                        survey.id,
                        survey.title,
                        surveyParticipant.boardingDate,
                        survey.region,
                        survey.createdAt,
                        survey.endDate,
                        getParticipationCount(),
                        survey.maxPassenger),
                getPassengerSumByBoardingType(BoardingType.UP),
                getPassengerSumByBoardingType(BoardingType.DOWN),
                getPassengerSumByBoardingType(BoardingType.ROUND));
    }

    private BooleanExpression getCreatedSurveyPagingCondition(final Long lastId, final LocalDate lastBoardingDate) {
        if (lastId == null && lastBoardingDate == null) {
            return null;
        }
        return survey.id.lt(lastId).or(survey.id.eq(lastId).and(surveyParticipant.boardingDate.gt(lastBoardingDate)));
    }

    private NumberExpression<Integer> getPassengerSumByBoardingType(final BoardingType boardingType) {
        return Expressions.cases()
                .when(surveyParticipant.boardingType.eq(boardingType))
                .then(surveyParticipant.passengerNum)
                .otherwise(0)
                .sumAggregate();
    }

    @Override
    public List<JoinSurveyResponse> findJoinSurveyList(final Long memberId, final Long lastId, final int pageSize) {
        return queryFactory
                .select(joinSurveyProjections())
                .from(survey)
                .join(surveyParticipant)
                .on(survey.id.eq(surveyParticipant.surveyId))
                .where(surveyParticipant.memberId.eq(memberId), getJoinSurveyPagingCondition(lastId))
                .orderBy(surveyParticipant.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private ConstructorExpression<JoinSurveyResponse> joinSurveyProjections() {
        return Projections.constructor(
                JoinSurveyResponse.class,
                Projections.constructor(
                        SurveyResponse.class,
                        survey.id,
                        survey.title,
                        surveyParticipant.boardingDate,
                        survey.region,
                        survey.createdAt,
                        survey.endDate,
                        getParticipationCount(),
                        survey.maxPassenger),
                surveyParticipant.id,
                surveyParticipant.createdAt,
                surveyParticipant.boardingType,
                surveyParticipant.passengerNum);
    }

    private Expression<Integer> getParticipationCount() {
        return ExpressionUtils.as(
                JPAExpressions.select(participantSub.passengerNum.sumAggregate())
                        .from(participantSub)
                        .where(participantSub
                                .surveyId
                                .eq(survey.id)
                                .and(participantSub.boardingDate.eq(surveyParticipant.boardingDate)))
                        .groupBy(participantSub.surveyId, participantSub.boardingDate),
                "participationCount");
    }

    private BooleanExpression getJoinSurveyPagingCondition(final Long lastId) {
        if (lastId == null) {
            return null;
        }
        return survey.id.lt(lastId);
    }
}
