package com.backend.allreva.recruitment.survey.query.model;

import com.backend.allreva.recruitment.survey.domain.SurveyBoardingDate;
import java.util.List;

public record SurveyDetail(
        Long surveyId, String title, List<SurveyBoardingDate> boardingDates, String information, boolean isClosed) {}
