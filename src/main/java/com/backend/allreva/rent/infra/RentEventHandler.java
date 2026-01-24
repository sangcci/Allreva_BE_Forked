package com.backend.allreva.rent.infra;



import com.backend.allreva.rent.command.domain.RentDeletedEvent;
import com.backend.allreva.rent.command.domain.RentSaveEvent;
import com.backend.allreva.module.search.domain.RentSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Service
public class RentEventHandler {

    private final RentSearchRepository rentDocumentRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMessage(final RentSaveEvent event) {
        rentDocumentRepository.save(event.to());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMessage(final RentDeletedEvent event) {
        rentDocumentRepository.deleteById(event.getRentId().toString());
    }
}