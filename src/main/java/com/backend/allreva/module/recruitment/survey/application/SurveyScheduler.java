package com.backend.allreva.module.recruitment.survey.application;

import com.backend.allreva.module.recruitment.survey.domain.SurveyRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SurveyScheduler {

    private final SurveyRepository surveyRepository;
    private static final String MIDNIGHT_CRON = "0 0 0 * * *";

    @Scheduled(cron = MIDNIGHT_CRON)
    public void closeSurvey() {
        try {
            surveyRepository.closeSurveys(LocalDate.now());
            log.info(" {} :daily survey close complete", LocalDate.now());
        } catch (Exception e) {
            log.error("can't close daily survey. Message: {}", e.getMessage());
        }
    }
}
