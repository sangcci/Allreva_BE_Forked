package com.backend.allreva.recruitment.survey.query.model;

import com.backend.allreva.recruitment.survey.domain.Region;
import java.time.LocalDate;

public record SurveySummary(Long surveyId, String title, Region region, int participationCount, LocalDate endDate) {}
