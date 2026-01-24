package com.backend.allreva.survey.infra;


import com.backend.allreva.survey.command.domain.SurveyDeletedEvent;
import com.backend.allreva.survey.command.domain.SurveySavedEvent;
import com.backend.allreva.module.search.domain.SurveySearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Service
public class SurveyEventHandler {

    private final SurveySearchRepository surveyDocumentRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMessage(final SurveySavedEvent event) {
        surveyDocumentRepository.save(event.to());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMessage(final SurveyDeletedEvent event) {
        surveyDocumentRepository.deleteById(event.getSurveyId().toString());
    }
}
