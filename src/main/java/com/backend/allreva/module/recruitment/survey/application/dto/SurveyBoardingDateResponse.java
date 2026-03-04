package com.backend.allreva.module.recruitment.survey.application.dto;

import java.time.LocalDate;

public record SurveyBoardingDateResponse(LocalDate date, int participationCount) {}
