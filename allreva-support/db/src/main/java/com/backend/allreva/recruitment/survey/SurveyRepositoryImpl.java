package com.backend.allreva.recruitment.survey;

import com.backend.allreva.recruitment.survey.domain.Survey;
import com.backend.allreva.recruitment.survey.domain.SurveyRepository;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SurveyRepositoryImpl implements SurveyRepository {

    private final SurveyJpaRepository surveyJpaRepository;

    @Override
    public Survey save(final Survey survey) {
        return surveyJpaRepository.save(SurveyEntity.from(survey)).toDomain();
    }

    @Override
    public Optional<Survey> findById(final Long id) {
        return surveyJpaRepository.findById(id).map(SurveyEntity::toDomain);
    }

    @Override
    public void delete(final Survey survey) {
        surveyJpaRepository.findById(survey.getId()).ifPresent(surveyJpaRepository::delete);
    }

    @Override
    public void closeSurveys(final LocalDate today) {
        surveyJpaRepository.closeSurveys(today);
    }
}
