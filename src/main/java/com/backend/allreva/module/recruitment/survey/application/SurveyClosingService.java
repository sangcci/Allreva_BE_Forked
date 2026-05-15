package com.backend.allreva.module.recruitment.survey.application;

import com.backend.allreva.module.recruitment.survey.domain.SurveyRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyClosingService {

    private final SurveyRepository surveyRepository;

    public void closeSurveys(final LocalDate targetDate) {
        try {
            surveyRepository.closeSurveys(targetDate);
            log.info(" {} :daily survey close complete", targetDate);
        } catch (Exception e) {
            log.error("can't close daily survey. Message: {}", e.getMessage());
        }
    }
}
