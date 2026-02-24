package com.backend.allreva.module.recruitment.survey.application.dto;

public record CreatedSurveyResponse(
        SurveyResponse surveyResponse,
        int upCount,
        int downCount,
        int roundCount) {
}
