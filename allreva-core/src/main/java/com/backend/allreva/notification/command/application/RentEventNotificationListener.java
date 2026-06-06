package com.backend.allreva.notification.command.application;

import com.backend.allreva.notification.command.implementation.NotificationNotifier;
import com.backend.allreva.notification.command.implementation.NotificationTargetReader;
import com.backend.allreva.notification.command.implementation.NotificationWriter;
import com.backend.allreva.notification.domain.Notification;
import com.backend.allreva.notification.domain.NotificationType;
import com.backend.allreva.recruitment.rent.command.event.RentJoinedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class RentEventNotificationListener {

    private final NotificationTargetReader targetReader;
    private final NotificationNotifier notificationNotifier;
    private final NotificationWriter notificationWriter;

    @Async("notificationExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onJoined(final RentJoinedEvent event) {
        try {
            Notification notification = Notification.builder()
                    .type(NotificationType.RENT_PARTICIPANT_JOINED)
                    .title("차량 대절 참여")
                    .message(event.getTitle() + " 차량 대절에 새로운 참여자가 등록되었습니다.")
                    .recipientId(event.getHostMemberId())
                    .senderId(event.getParticipantMemberId())
                    .resourceId(event.getRentId())
                    .resourceName(event.getTitle())
                    .read(false)
                    .build();

            targetReader.get(event.getHostMemberId()).ifPresent(target -> notify(notification, target));
        } catch (Exception e) {
            log.error("차량 대절 참여 알림 처리 실패. rentId: {}", event.getRentId(), e);
        }
    }

    private void notify(final Notification notification, final String target) {
        if (notificationNotifier.notify(notification, target)) {
            notificationWriter.save(notification);
        }
    }
}
