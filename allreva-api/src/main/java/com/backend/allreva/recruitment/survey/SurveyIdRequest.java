package com.backend.allreva.recruitment.survey;

import com.backend.allreva.recruitment.survey.command.input.SurveyIdCommand;
import jakarta.validation.constraints.NotNull;

public record SurveyIdRequest(@NotNull Long surveyId) {

    public SurveyIdCommand toCommand() {
        return new SurveyIdCommand(surveyId);
    }
}
