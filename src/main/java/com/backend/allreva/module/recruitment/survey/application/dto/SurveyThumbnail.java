package com.backend.allreva.module.recruitment.survey.application.dto;

import java.time.LocalDate;

public record SurveyThumbnail(Long id, String title, String region, Integer participantNum, LocalDate edDate) {}
