package com.backend.allreva.module.notification.infra.sse;

import com.backend.allreva.module.notification.application.port.NotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** SSE 기반 알림 전송 구현체 NotificationSender 인터페이스를 구현하여 FCM과 동일한 추상화 레벨 제공 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SseSender implements NotificationSender {

    public static final String SSE_EVENT_NAME = "notification";

    private final SseConnectionManager sseConnectionManager;

    /**
     * SSE를 통해 실시간 알림 전송
     *
     * @param target memberId (문자열)
     * @param title 알림 제목
     * @param message 알림 메시지
     */
    @Override
    public void sendMessage(String target, String title, String message) {
        try {
            Long memberId = Long.parseLong(target);

            // SSE 연결이 있는 경우에만 전송
            if (sseConnectionManager.isConnected(memberId)) {
                sseConnectionManager.sendNotification(memberId, title, message);
                log.debug("SSE 알림 전송 성공 - memberId: {}, title: {}", memberId, title);
            } else {
                log.debug("SSE 연결 없음 - memberId: {}", memberId);
            }

        } catch (NumberFormatException e) {
            log.warn("잘못된 SSE target 형식: {}", target);
            throw new IllegalArgumentException("SSE target은 숫자(memberId)여야 합니다: " + target, e);
        }
    }
}
