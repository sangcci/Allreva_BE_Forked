package com.backend.allreva.recruitment.survey.query.model;

import com.backend.allreva.recruitment.survey.domain.BoardingType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public final class JoinedSurvey {

    private final SurveyItem surveyItem;
    private final Long surveyParticipantId;
    private final LocalDate applyDate;
    private final BoardingType boardingType;
    private final Integer passengerNum;

    public JoinedSurvey(
            final SurveyItem surveyItem,
            final Long surveyParticipantId,
            final LocalDateTime applyDate,
            final BoardingType boardingType,
            final int passengerNum) {
        this.surveyItem = surveyItem;
        this.surveyParticipantId = surveyParticipantId;
        this.applyDate = applyDate.toLocalDate();
        this.boardingType = boardingType;
        this.passengerNum = passengerNum;
    }

    public JoinedSurvey(
            final SurveyItem surveyItem,
            final Long surveyParticipantId,
            final LocalDate applyDate,
            final BoardingType boardingType,
            final Integer passengerNum) {
        this.surveyItem = surveyItem;
        this.surveyParticipantId = surveyParticipantId;
        this.applyDate = applyDate;
        this.boardingType = boardingType;
        this.passengerNum = passengerNum;
    }
}
