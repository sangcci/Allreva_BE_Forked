package com.backend.allreva.module.recruitment.survey.application.dto;

import com.backend.allreva.module.recruitment.survey.domain.value.Region;
import java.time.LocalDate;

public record SurveyMainResponse(Long id, String title, Region region, Integer participationCount, LocalDate edDate) {
    public static SurveyMainResponse of(
            final Long id,
            final String title,
            final Region region,
            final Integer participationCount,
            final LocalDate edDate) {
        return new SurveyMainResponse(id, title, region, participationCount, edDate);
    }
}
