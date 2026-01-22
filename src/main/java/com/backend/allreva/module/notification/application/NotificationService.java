package com.backend.allreva.module.notification.application;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.member.command.domain.Member;
import com.backend.allreva.module.notification.application.dto.NotificationTargetRequest;
import com.backend.allreva.module.notification.application.dto.NotificationIdRequest;
import com.backend.allreva.module.notification.application.port.NotificationSender;
import com.backend.allreva.module.notification.application.port.NotificationTargetStorage;
import com.backend.allreva.module.notification.domain.Notification;
import com.backend.allreva.module.notification.domain.NotificationEvent;
import com.backend.allreva.module.notification.domain.NotificationRepository;
import com.backend.allreva.module.notification.exception.NotificationErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationSender notificationSender;
    private final NotificationRepository notificationRepository;
    private final NotificationTargetStorage targetStorage;

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByRecipientId(final Member member, final Long lastId,
            final int pageSize) {
        return notificationRepository.findNotificationsByRecipientId(member.getId(), lastId, pageSize);
    }

    @Transactional
    public void markAsRead(final Member member, final NotificationIdRequest notificationIdRequest) {
        Notification notification = notificationRepository
                .findByIdAndRecipientId(notificationIdRequest.id(), member.getId())
                .orElseThrow(() -> new CustomException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));
        notification.read();
    }

    public void registerTarget(final Member member, final NotificationTargetRequest notificationTargetRequest) {
        targetStorage.save(member.getId(), notificationTargetRequest.target());
    }

    public void deleteTarget(final Member member) {
        targetStorage.delete(member.getId());
    }

    @Async
    @TransactionalEventListener
    public void sendMessage(final NotificationEvent event) {
        try {
            // 알림 대상 가져오기
            List<String> targets = Optional
                    .ofNullable(targetStorage.findTargetsByMemberIds(event.getRecipientIds()))
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .filter(Objects::nonNull)
                    .toList();

            if (targets.isEmpty()) {
                log.warn("알림 대상이 없습니다.");
                return;
            }

            // 알림 메세지 전송 (실패한 대상은 로그만 남기고 계속 진행)
            int successCount = 0;
            int failureCount = 0;

            for (String target : targets) {
                try {
                    notificationSender.sendMessage(target, event.getTitle(), event.getMessage());
                    successCount++;
                } catch (Exception e) {
                    log.warn("알림 전송 실패 - title: {}", event.getTitle());
                    failureCount++;
                }
            }

            log.info("알림 메시지 전송 완료 - 성공: {}, 실패: {}", successCount, failureCount);

            // 알림 메세지 저장 (전송 실패해도 알림 내역은 저장)
            List<Notification> notificationEntities = event.getRecipientIds().stream()
                    .map(recipientId -> Notification.from(event.getTitle(), event.getMessage(), recipientId))
                    .toList();
            notificationRepository.saveAll(notificationEntities);
            log.debug("알림 메세지 저장 완료");

        } catch (Exception e) {
            // 전체 프로세스 실패
            log.error("알림 전송 프로세스 실패 - title: {}", event.getTitle(), e);
            // 이벤트 리스너이므로 예외를 던지지 않음 (트랜잭션 롤백 방지)
        }
    }
}
