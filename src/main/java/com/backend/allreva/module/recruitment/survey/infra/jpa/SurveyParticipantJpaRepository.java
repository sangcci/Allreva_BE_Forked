package com.backend.allreva.module.recruitment.survey.infra.jpa;

import com.backend.allreva.module.recruitment.survey.domain.participant.SurveyParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyParticipantJpaRepository extends JpaRepository<SurveyParticipant, Long> {
}
