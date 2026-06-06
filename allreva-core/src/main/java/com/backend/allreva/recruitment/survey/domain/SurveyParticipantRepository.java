package com.backend.allreva.recruitment.survey.domain;

import java.util.Optional;

public interface SurveyParticipantRepository {

    SurveyParticipant save(SurveyParticipant participant);

    Optional<SurveyParticipant> findById(Long id);

    void delete(SurveyParticipant participant);

    boolean existsByMemberIdAndSurveyId(Long memberId, Long surveyId);
}
