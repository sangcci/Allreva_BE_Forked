package com.backend.allreva.survey.query.application.response;

import com.backend.allreva.survey.command.domain.value.Region;

import java.time.LocalDate;

public record SurveyMainResponse(
        Long id,
        String title,
        Region region,
        Integer participationCount,
        LocalDate edDate
) {
    public static SurveyMainResponse of(
            final Long id,
            final String title,
            final Region region,
            final Integer participationCount,
            final LocalDate edDate
    ) {
        return new SurveyMainResponse(id, title, region, participationCount, edDate);
    }
}
