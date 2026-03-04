package com.backend.allreva.module.recruitment.survey.domain;

import com.backend.allreva.module.recruitment.survey.application.dto.SortType;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveyDetailResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveyMainResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveySummaryResponse;
import com.backend.allreva.module.recruitment.survey.domain.value.Region;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SurveyRepository {

    Survey save(Survey survey);

    Optional<Survey> findById(Long id);

    void delete(Survey survey);

    void closeSurveys(LocalDate today);

    SurveyDetailResponse findSurveyDetail(Long surveyId);

    List<SurveySummaryResponse> findSurveyList(
            Region region, SortType sortType, Long lastId, LocalDate lastEndDate, int pageSize);

    List<SurveySummaryResponse> findSurveyMainList();

    Optional<SurveyMainResponse> findSurveyWithParticipationCount(Long surveyId);
}
