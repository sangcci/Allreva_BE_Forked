package com.backend.allreva.recruitment.survey.query.model;

import java.time.LocalDate;

public record SurveyThumbnail(Long id, String title, String region, Integer participantNum, LocalDate edDate) {}
