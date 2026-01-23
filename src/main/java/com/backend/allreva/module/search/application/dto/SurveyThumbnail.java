package com.backend.allreva.module.search.application.dto;

import com.backend.allreva.module.search.domain.SurveyDocument;

import java.time.LocalDate;

public record SurveyThumbnail(
        Long id,
        String title,
        String region,
        Integer participantNum,
        LocalDate edDate
) {
        public static SurveyThumbnail from(final SurveyDocument surveyDocument) {
            return new SurveyThumbnail(
                    Long.parseLong(surveyDocument.getId()),
                    surveyDocument.getTitle(),
                    surveyDocument.getRegion(),
                    surveyDocument.getParticipationCount(),
                    surveyDocument.getEdDate()
            );
        }
}
