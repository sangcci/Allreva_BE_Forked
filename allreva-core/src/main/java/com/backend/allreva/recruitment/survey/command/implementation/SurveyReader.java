package com.backend.allreva.recruitment.survey.command.implementation;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.recruitment.survey.domain.Survey;
import com.backend.allreva.recruitment.survey.domain.SurveyErrorCode;
import com.backend.allreva.recruitment.survey.domain.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SurveyReader {

    private final SurveyRepository surveyRepository;

    public Survey get(final Long surveyId) {
        return surveyRepository
                .findById(surveyId)
                .orElseThrow(() -> new CustomException(SurveyErrorCode.SURVEY_NOT_FOUND));
    }
}
