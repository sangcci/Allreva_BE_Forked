package com.backend.allreva.recruitment.survey.query.application;

import com.backend.allreva.common.event.Events;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.recruitment.survey.domain.Region;
import com.backend.allreva.recruitment.survey.domain.SortType;
import com.backend.allreva.recruitment.survey.query.implementation.SurveyFinderPort;
import com.backend.allreva.recruitment.survey.query.implementation.SurveyParticipationFinderPort;
import com.backend.allreva.recruitment.survey.query.model.CreatedSurvey;
import com.backend.allreva.recruitment.survey.query.model.JoinedSurvey;
import com.backend.allreva.recruitment.survey.query.model.SurveyDetail;
import com.backend.allreva.recruitment.survey.query.model.SurveyMain;
import com.backend.allreva.recruitment.survey.query.model.SurveySummary;
import com.backend.allreva.recruitment.survey.query.model.SurveyThumbnail;
import com.backend.allreva.search.domain.KeywordSearchedEvent;
import com.backend.allreva.search.domain.SearchErrorCode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyFinder {

    private final SurveyFinderPort surveyFinder;
    private final SurveyParticipationFinderPort surveyParticipationFinder;

    public List<SurveyThumbnail> getSurveySuggestions(final String title) {
        Events.raise(new KeywordSearchedEvent(title));
        List<SurveyThumbnail> thumbnails = surveyFinder.findThumbnailsByTitle(title, 2);
        if (thumbnails.isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return thumbnails;
    }

    public SliceResponse<SurveyThumbnail, Long> searchSurveys(final String title, final Long cursorId, final int size) {
        SliceResponse<SurveyThumbnail, Long> response = surveyFinder.findAllByTitle(title, cursorId, size);
        if (response.items().isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return response;
    }

    public List<CreatedSurvey> findCreatedSurveyList(
            final Long memberId, final Long lastId, final LocalDate lastBoardingDate, final int pageSize) {
        return surveyParticipationFinder.findCreatedSurveyList(memberId, lastId, lastBoardingDate, pageSize);
    }

    public List<JoinedSurvey> findJoinSurveyList(final Long memberId, final Long lastId, final int pageSize) {
        return surveyParticipationFinder.findJoinSurveyList(memberId, lastId, pageSize);
    }

    public SurveyDetail findSurveyDetail(final Long surveyId) {
        return surveyFinder.findSurveyDetail(surveyId);
    }

    public List<SurveySummary> findSurveyList(
            final Region region,
            final SortType sortType,
            final Long lastId,
            final LocalDate lastEndDate,
            final int pageSize) {
        return surveyFinder.findSurveyList(region, sortType, lastId, lastEndDate, pageSize);
    }

    public Optional<SurveyMain> findSurveyWithParticipationCount(final Long surveyId) {
        return surveyFinder.findSurveyWithParticipationCount(surveyId);
    }

    public List<SurveySummary> findSurveyMainList() {
        return surveyFinder.findSurveyMainList();
    }
}
