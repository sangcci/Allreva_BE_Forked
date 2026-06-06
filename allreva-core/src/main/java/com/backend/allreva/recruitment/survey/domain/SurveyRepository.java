package com.backend.allreva.recruitment.survey.domain;

import java.time.LocalDate;
import java.util.Optional;

public interface SurveyRepository {

    Survey save(Survey survey);

    Optional<Survey> findById(Long id);

    void delete(Survey survey);

    void closeSurveys(LocalDate today);
}
