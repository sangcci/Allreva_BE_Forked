package com.backend.allreva.recruitment.survey.command.implementation;

import com.backend.allreva.recruitment.survey.domain.Survey;
import com.backend.allreva.recruitment.survey.domain.SurveyParticipant;
import com.backend.allreva.recruitment.survey.domain.SurveyParticipantRepository;
import com.backend.allreva.recruitment.survey.domain.SurveyRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SurveyWriter {

    private final SurveyRepository surveyRepository;
    private final SurveyParticipantRepository surveyParticipantRepository;

    public Survey save(final Survey survey) {
        return surveyRepository.save(survey);
    }

    public SurveyParticipant save(final SurveyParticipant participant) {
        return surveyParticipantRepository.save(participant);
    }

    public void delete(final Survey survey) {
        surveyRepository.delete(survey);
    }

    public void delete(final SurveyParticipant participant) {
        surveyParticipantRepository.delete(participant);
    }

    public void closeExpired(final LocalDate today) {
        surveyRepository.closeSurveys(today);
    }
}
