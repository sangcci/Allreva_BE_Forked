package com.backend.allreva.recruitment.survey.query.implementation;

import com.backend.allreva.recruitment.survey.query.model.CreatedSurvey;
import com.backend.allreva.recruitment.survey.query.model.JoinedSurvey;
import java.time.LocalDate;
import java.util.List;

public interface SurveyParticipationFinderPort {

    List<CreatedSurvey> findCreatedSurveyList(Long memberId, Long lastId, LocalDate lastBoardingDate, int pageSize);

    List<JoinedSurvey> findJoinSurveyList(Long memberId, Long lastId, int pageSize);
}
