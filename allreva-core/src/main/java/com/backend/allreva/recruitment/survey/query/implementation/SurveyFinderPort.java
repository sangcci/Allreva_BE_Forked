package com.backend.allreva.recruitment.survey.query.implementation;

import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.recruitment.survey.domain.Region;
import com.backend.allreva.recruitment.survey.domain.SortType;
import com.backend.allreva.recruitment.survey.query.model.SurveyDetail;
import com.backend.allreva.recruitment.survey.query.model.SurveyMain;
import com.backend.allreva.recruitment.survey.query.model.SurveySummary;
import com.backend.allreva.recruitment.survey.query.model.SurveyThumbnail;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SurveyFinderPort {

    List<SurveyThumbnail> findThumbnailsByTitle(String title, int limit);

    SliceResponse<SurveyThumbnail, Long> findAllByTitle(String query, Long cursorId, int pageSize);

    SurveyDetail findSurveyDetail(Long surveyId);

    List<SurveySummary> findSurveyList(
            Region region, SortType sortType, Long lastId, LocalDate lastEndDate, int pageSize);

    List<SurveySummary> findSurveyMainList();

    Optional<SurveyMain> findSurveyWithParticipationCount(Long surveyId);
}
