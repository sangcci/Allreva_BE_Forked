package com.backend.allreva.recruitment.survey;

import com.backend.allreva.recruitment.survey.command.input.JoinSurveyCommand;
import com.backend.allreva.recruitment.survey.domain.BoardingType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record JoinSurveyRequest(
        @NotNull Long surveyId,
        @NotNull LocalDate boardingDate,
        @NotNull BoardingType boardingType,

        @NotNull @Min(value = 1, message = "탑승 인원 수는 1명 이상이어야 합니다.")
        Integer passengerNum,

        boolean notified) {

    public JoinSurveyCommand toCommand() {
        return new JoinSurveyCommand(surveyId, boardingDate, boardingType, passengerNum, notified);
    }
}
