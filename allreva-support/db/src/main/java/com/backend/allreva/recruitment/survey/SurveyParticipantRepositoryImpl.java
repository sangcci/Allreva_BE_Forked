package com.backend.allreva.recruitment.survey;

import com.backend.allreva.recruitment.survey.domain.SurveyParticipant;
import com.backend.allreva.recruitment.survey.domain.SurveyParticipantRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SurveyParticipantRepositoryImpl implements SurveyParticipantRepository {

    private final SurveyParticipantJpaRepository surveyParticipantJpaRepository;

    @Override
    public SurveyParticipant save(final SurveyParticipant participant) {
        return surveyParticipantJpaRepository
                .save(SurveyParticipantEntity.from(participant))
                .toDomain();
    }

    @Override
    public Optional<SurveyParticipant> findById(final Long id) {
        return surveyParticipantJpaRepository.findById(id).map(SurveyParticipantEntity::toDomain);
    }

    @Override
    public void delete(final SurveyParticipant participant) {
        surveyParticipantJpaRepository.findById(participant.getId()).ifPresent(surveyParticipantJpaRepository::delete);
    }

    @Override
    public boolean existsByMemberIdAndSurveyId(final Long memberId, final Long surveyId) {
        return surveyParticipantJpaRepository.existsByMemberIdAndSurveyId(memberId, surveyId);
    }
}
