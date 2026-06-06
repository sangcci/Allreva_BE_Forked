package com.backend.allreva.recruitment.survey;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyParticipantJpaRepository extends JpaRepository<SurveyParticipantEntity, Long> {

    boolean existsByMemberIdAndSurveyId(Long memberId, Long surveyId);
}
