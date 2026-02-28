package com.backend.allreva.module.recruitment.survey.application.dto;

import jakarta.validation.constraints.NotNull;

public record SurveyIdRequest(@NotNull Long surveyId) {}
