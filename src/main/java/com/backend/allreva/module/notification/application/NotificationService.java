package com.backend.allreva.module.notification.application;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.notification.application.dto.NotificationTargetRequest;
import com.backend.allreva.module.notification.application.dto.NotificationIdRequest;
import com.backend.allreva.module.notification.application.port.NotificationSender;
import com.backend.allreva.module.notification.application.port.NotificationTargetStorage;
import com.backend.allreva.module.notification.domain.Notification;
import com.backend.allreva.module.notification.domain.event.NotificationEvent;
import com.backend.allreva.module.notification.domain.NotificationRepository;
import com.backend.allreva.module.notification.domain.value.NotificationType;
import com.backend.allreva.module.notification.exception.NotificationErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final List<NotificationSender> notificationSenders;
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

    /**
     * 모든 알림 이벤트를 통합 처리
     * - 알림 타입에 따라 적절한 포매팅 적용
     * - 모든 NotificationSender로 전송 (FCM, SSE)
     * - 채팅 메시지는 내역 저장 제외 (MongoDB에 이미 저장됨)
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendMessage(final NotificationEvent event) {
        try {
            log.info("알림 이벤트 수신 - type: {}, recipients: {}",
                     event.getType(), event.getRecipientIds().size());

            // 1. 알림 메시지 포매팅
            String title = formatTitle(event);
            String message = formatMessage(event);

            // 2. 모든 Sender로 전송 (FCM + SSE)
            sendToAllSenders(event.getRecipientIds(), title, message);

            // 3. 알림 내역 저장 (채팅 메시지는 제외 - MongoDB에 이미 저장됨)
            if (!isChatMessage(event.getType())) {
                saveNotificationHistory(event, title, message);
            }

        } catch (Exception e) {
            // 전체 프로세스 실패
            log.error("알림 전송 프로세스 실패 - type: {}", event.getType(), e);
            // 이벤트 리스너이므로 예외를 던지지 않음 (트랜잭션 롤백 방지)
        }
    }

    /**
     * 알림 타입에 따라 제목 포매팅
     */
    private String formatTitle(NotificationEvent event) {
        return switch (event.getType()) {
            case CHAT_MESSAGE -> event.getRoomName();  // "채팅방 이름"
            case CHAT_MEMBER_JOINED, CHAT_MEMBER_LEFT ->
                event.getRoomName() + " 알림";
            case RENT_REGISTERED -> "차량 대절 등록";
            case RENT_PARTICIPANT_JOINED -> "차량 대절 참여";
            case RENT_CANCELLED -> "차량 대절 취소";
            case SURVEY_REGISTERED -> "수요조사 등록";
            case SURVEY_RESPONSE_RECEIVED -> "수요조사 응답";
            case SURVEY_CLOSED -> "수요조사 마감";
            case CONCERT_REMINDER -> "콘서트 리마인더";
            case CONCERT_UPDATED -> "콘서트 정보 변경";
            case DIARY_LIKE_RECEIVED -> "일기 좋아요";
            case DIARY_COMMENT_RECEIVED -> "일기 댓글";
            default -> "새로운 알림";
        };
    }

    /**
     * 알림 타입에 따라 메시지 포매팅
     */
    private String formatMessage(NotificationEvent event) {
        return switch (event.getType()) {
            case CHAT_MESSAGE ->
                event.getSenderName() + ": " + event.getContent();
            case CHAT_MEMBER_JOINED, CHAT_MEMBER_LEFT ->
                event.getContent();
            default -> event.getContent();
        };
    }

    /**
     * 모든 NotificationSender로 알림 전송
     */
    private void sendToAllSenders(List<Long> recipientIds, String title, String message) {
        // FCM 토큰 조회 (FCM용)
        List<String> fcmTargets = Optional
                .ofNullable(targetStorage.findTargetsByMemberIds(recipientIds))
                .orElseGet(Collections::emptyList)
                .stream()
                .filter(Objects::nonNull)
                .toList();

        int totalSuccess = 0;
        int totalFailure = 0;

        for (NotificationSender sender : notificationSenders) {
            String senderName = sender.getClass().getSimpleName();

            try {
                // FCM은 토큰 사용, SSE는 memberId 사용
                List<String> targets = isFcmSender(sender) ? fcmTargets :
                        recipientIds.stream().map(String::valueOf).toList();

                if (targets.isEmpty()) {
                    log.warn("{} - 알림 대상이 없습니다.", senderName);
                    continue;
                }

                int successCount = 0;
                int failureCount = 0;

                for (String target : targets) {
                    try {
                        sender.sendMessage(target, title, message);
                        successCount++;
                    } catch (Exception e) {
                        log.debug("{} - 알림 전송 실패: {}", senderName, target);
                        failureCount++;
                    }
                }

                totalSuccess += successCount;
                totalFailure += failureCount;

                log.info("{} - 전송 완료 (성공: {}, 실패: {})", senderName, successCount, failureCount);

            } catch (Exception e) {
                log.warn("{} - 전송 프로세스 실패", senderName, e);
            }
        }

        log.info("전체 알림 전송 완료 - 성공: {}, 실패: {}", totalSuccess, totalFailure);
    }

    /**
     * 알림 내역 저장
     */
    private void saveNotificationHistory(NotificationEvent event, String title, String message) {
        try {
            List<Notification> notifications = event.getRecipientIds().stream()
                    .map(recipientId -> Notification.fromEvent(event, recipientId, title, message))
                    .toList();
            notificationRepository.saveAll(notifications);
            log.debug("알림 내역 저장 완료 - count: {}", notifications.size());
        } catch (Exception e) {
            log.error("알림 내역 저장 실패", e);
        }
    }

    /**
     * 채팅 메시지 알림인지 확인
     */
    private boolean isChatMessage(NotificationType type) {
        return type == NotificationType.CHAT_MESSAGE;
    }

    /**
     * FCM Sender인지 확인
     */
    private boolean isFcmSender(NotificationSender sender) {
        return sender.getClass().getSimpleName().contains("Fcm");
    }
}
