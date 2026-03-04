package com.backend.allreva.module.recruitment.survey.application.dto;

import com.backend.allreva.module.recruitment.survey.domain.value.Region;
import java.time.LocalDate;

public record SurveySummaryResponse(
        Long surveyId, String title, Region region, int participationCount, LocalDate endDate) {}
