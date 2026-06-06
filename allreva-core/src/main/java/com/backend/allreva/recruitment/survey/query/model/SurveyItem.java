package com.backend.allreva.recruitment.survey.query.model;

import com.backend.allreva.recruitment.survey.domain.Region;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public final class SurveyItem {
    private final Long surveyId;
    private final String title;
    private final LocalDate boardingDate;
    private final Region region;
    private final LocalDate surveyStartDate;
    private final LocalDate surveyEndDate;
    private final Integer participationCount;
    private final Integer maxPassenger;

    public SurveyItem(
            final Long surveyId,
            final String title,
            final LocalDate boardingDate,
            final Region region,
            final LocalDateTime surveyStartDate,
            final LocalDate surveyEndDate,
            final int participationCount,
            final int maxPassenger) {
        this.surveyId = surveyId;
        this.title = title;
        this.boardingDate = boardingDate;
        this.region = region;
        this.surveyStartDate = surveyStartDate.toLocalDate();
        this.surveyEndDate = surveyEndDate;
        this.participationCount = participationCount;
        this.maxPassenger = maxPassenger;
    }

    public SurveyItem(
            final Long surveyId,
            final String title,
            final LocalDate boardingDate,
            final Region region,
            final LocalDate surveyStartDate,
            final LocalDate surveyEndDate,
            final Integer participationCount,
            final Integer maxPassenger) {
        this.surveyId = surveyId;
        this.title = title;
        this.boardingDate = boardingDate;
        this.region = region;
        this.surveyStartDate = surveyStartDate;
        this.surveyEndDate = surveyEndDate;
        this.participationCount = participationCount;
        this.maxPassenger = maxPassenger;
    }
}
