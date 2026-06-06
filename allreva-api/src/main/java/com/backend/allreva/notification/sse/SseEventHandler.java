package com.backend.allreva.notification.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class SseEventHandler {

    private final SseConnectionManager sseConnectionManager;

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMessage(final SseTimedOutEvent event) {
        Long memberId = event.getMemberId();
        sseConnectionManager.disconnect(memberId);
    }
}
