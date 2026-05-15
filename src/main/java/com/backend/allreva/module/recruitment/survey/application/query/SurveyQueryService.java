package com.backend.allreva.module.recruitment.survey.application.query;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.events.Events;
import com.backend.allreva.module.recruitment.survey.application.dto.CreatedSurveyResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.JoinSurveyResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.SortType;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveyDetailResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveyMainResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveySummaryResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveyThumbnail;
import com.backend.allreva.module.recruitment.survey.application.port.SurveySearchRepository;
import com.backend.allreva.module.recruitment.survey.domain.SurveyRepository;
import com.backend.allreva.module.recruitment.survey.domain.participant.SurveyParticipantRepository;
import com.backend.allreva.module.recruitment.survey.domain.value.Region;
import com.backend.allreva.module.search.domain.event.KeywordSearchedEvent;
import com.backend.allreva.module.search.exception.SearchErrorCode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SurveyQueryService {

    private final SurveyRepository surveyRepository;
    private final SurveyParticipantRepository surveyParticipantRepository;
    private final SurveySearchRepository surveySearchRepository;

    public List<SurveyThumbnail> getSurveySuggestions(final String title) {
        Events.raise(new KeywordSearchedEvent(title));
        List<SurveyThumbnail> thumbnails = surveySearchRepository.findThumbnailsByTitle(title, 2);
        if (thumbnails.isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return thumbnails;
    }

    public SliceResponse<SurveyThumbnail, Long> searchSurveys(final String title, final Long cursorId, final int size) {
        SliceResponse<SurveyThumbnail, Long> response = surveySearchRepository.findAllByTitle(title, cursorId, size);
        if (response.items().isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return response;
    }

    public List<CreatedSurveyResponse> findCreatedSurveyList(
            final Long memberId, final Long lastId, final LocalDate lastBoardingDate, final int pageSize) {
        return surveyParticipantRepository.findCreatedSurveyList(memberId, lastId, lastBoardingDate, pageSize);
    }

    public List<JoinSurveyResponse> findJoinSurveyList(final Long memberId, final Long lastId, final int pageSize) {
        return surveyParticipantRepository.findJoinSurveyList(memberId, lastId, pageSize);
    }

    public SurveyDetailResponse findSurveyDetail(final Long surveyId) {
        return surveyRepository.findSurveyDetail(surveyId);
    }

    public List<SurveySummaryResponse> findSurveyList(
            final Region region,
            final SortType sortType,
            final Long lastId,
            final LocalDate lastEndDate,
            final int pageSize) {
        return surveyRepository.findSurveyList(region, sortType, lastId, lastEndDate, pageSize);
    }

    public Optional<SurveyMainResponse> findSurveyWithParticipationCount(final Long surveyId) {
        return surveyRepository.findSurveyWithParticipationCount(surveyId);
    }

    public List<SurveySummaryResponse> findSurveyMainList() {
        return surveyRepository.findSurveyMainList();
    }
}
