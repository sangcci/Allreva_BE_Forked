package com.backend.allreva.module.recruitment.survey.application.dto;

import com.backend.allreva.module.recruitment.survey.domain.value.BoardingType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public final class JoinSurveyResponse {

    private final SurveyResponse surveyResponse;
    private final Long surveyParticipantId;
    private final LocalDate applyDate;
    private final BoardingType boardingType;
    private final Integer passengerNum;

    public JoinSurveyResponse(
            final SurveyResponse surveyResponse,
            final Long surveyParticipantId,
            final LocalDateTime applyDate,
            final BoardingType boardingType,
            final int passengerNum) {
        this.surveyResponse = surveyResponse;
        this.surveyParticipantId = surveyParticipantId;
        this.applyDate = applyDate.toLocalDate();
        this.boardingType = boardingType;
        this.passengerNum = passengerNum;
    }
}
