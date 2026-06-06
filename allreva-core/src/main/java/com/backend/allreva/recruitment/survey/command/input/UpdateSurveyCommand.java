package com.backend.allreva.recruitment.survey.command.input;

import com.backend.allreva.recruitment.survey.domain.Region;
import java.time.LocalDate;
import java.util.List;

public record UpdateSurveyCommand(
        Long surveyId,
        String title,
        List<LocalDate> boardingDates,
        Region region,
        LocalDate endDate,

        int maxPassenger,

        String information) {}
