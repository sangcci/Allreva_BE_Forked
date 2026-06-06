package com.backend.allreva.batch.survey;

import com.backend.allreva.recruitment.survey.command.application.SurveyService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SurveyClosingScheduler {

    private static final String MIDNIGHT_CRON = "0 0 0 * * *";

    private final SurveyService surveyService;

    @Scheduled(cron = MIDNIGHT_CRON)
    public void closeExpiredSurveys() {
        LocalDate today = LocalDate.now();
        try {
            surveyService.closeExpired(today);
            log.info("{}: daily survey close complete", today);
        } catch (Exception e) {
            log.error("Can't close daily survey. Message: {}", e.getMessage(), e);
        }
    }
}
