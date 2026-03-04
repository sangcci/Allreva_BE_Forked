package com.backend.allreva.module.search.application.dto;

import java.util.List;

public record SurveySearchListResponse(List<SurveyThumbnail> surveyThumbnails, Long nextCursorId) {
    public static SurveySearchListResponse from(final List<SurveyThumbnail> surveyThumbnails, final Long nextCursorId) {
        return new SurveySearchListResponse(surveyThumbnails, nextCursorId);
    }
}
