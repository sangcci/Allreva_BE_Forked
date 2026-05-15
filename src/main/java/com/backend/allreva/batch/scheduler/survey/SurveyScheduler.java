package com.backend.allreva.batch.scheduler.survey;

import com.backend.allreva.module.recruitment.survey.application.SurveyClosingService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SurveyScheduler {

    private static final String MIDNIGHT_CRON = "0 0 0 * * *";

    private final SurveyClosingService surveyClosingService;

    @Scheduled(cron = MIDNIGHT_CRON)
    public void closeSurvey() {
        surveyClosingService.closeSurveys(LocalDate.now());
    }
}
