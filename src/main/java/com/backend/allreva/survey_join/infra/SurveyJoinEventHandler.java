package com.backend.allreva.survey_join.infra;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.survey.exception.SurveyErrorCode;
import com.backend.allreva.module.search.domain.SurveyDocument;
import com.backend.allreva.module.search.domain.SurveySearchRepository;
import com.backend.allreva.survey_join.command.domain.SurveyJoinEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SurveyJoinEventHandler {

    private final SurveySearchRepository surveySearchRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMessage(final SurveyJoinEvent event) {
        Long surveyId = event.getSurveyId();
        SurveyDocument surveyDocument = surveySearchRepository.findById(surveyId.toString())
                .orElseThrow(() -> new CustomException(SurveyErrorCode.SURVEY_NOT_FOUND));

        surveyDocument.updateParticipationCount(event.getParticipationCount());
        surveySearchRepository.save(surveyDocument);
    }
}
