package com.backend.allreva.module.recruitment.survey.application.dto;

import java.util.List;

public record SurveyDetailResponse(
        Long surveyId,
        String title,
        List<SurveyBoardingDateResponse> boardingDates,
        String information,
        boolean isClosed) {}
