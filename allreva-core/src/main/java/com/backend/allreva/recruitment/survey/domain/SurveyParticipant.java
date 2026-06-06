package com.backend.allreva.recruitment.survey.domain;

import com.backend.allreva.common.exception.CustomException;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SurveyParticipant {

    private Long id;
    private Long surveyId;
    private Long memberId;
    private LocalDate boardingDate;
    private BoardingType boardingType;
    private int passengerNum;
    private boolean notified;

    @Builder
    private SurveyParticipant(
            final Long id,
            final Long surveyId,
            final Long memberId,
            final LocalDate boardingDate,
            final BoardingType boardingType,
            final int passengerNum,
            final boolean notified) {
        this.id = id;
        this.surveyId = surveyId;
        this.memberId = memberId;
        this.boardingDate = boardingDate;
        this.boardingType = boardingType;
        this.passengerNum = passengerNum;
        this.notified = notified;
    }

    public void validateOwner(final Long memberId) {
        if (!this.memberId.equals(memberId)) {
            throw new CustomException(SurveyErrorCode.SURVEY_JOIN_ACCESS_DENIED);
        }
    }

    public void markNotified() {
        this.notified = true;
    }

    public boolean isNotified() {
        return notified;
    }
}
