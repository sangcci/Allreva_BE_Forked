package com.backend.allreva.module.recruitment.survey.domain.participant;

import com.backend.allreva.module.recruitment.survey.application.dto.CreatedSurveyResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.JoinSurveyResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SurveyParticipantRepository {

    SurveyParticipant save(SurveyParticipant participant);

    Optional<SurveyParticipant> findById(Long id);

    void delete(SurveyParticipant participant);

    List<CreatedSurveyResponse> findCreatedSurveyList(
            Long memberId, Long lastId, LocalDate lastBoardingDate, int pageSize);

    List<JoinSurveyResponse> findJoinSurveyList(Long memberId, Long lastId, int pageSize);

    boolean existsByMemberIdAndSurveyId(Long memberId, Long surveyId);
}
