package com.backend.allreva.module.notification.infra.sse;

import com.backend.allreva.common.event.Events;
import com.backend.allreva.module.notification.infra.sse.event.SseTimedOutEvent;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@Component
public class SseConnectionManager {

    public static final String SSE_NAME = "SSE_PreviewMessage";
    public static final String INIT_MESSAGE = "채팅 SSE 연결";

    public static final Long TIME_LIMIT = 60000 * 60 * 24L;

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    private SseEmitter createSseEmitter(final Long memberId) {
        SseEmitter emitter = new SseEmitter(TIME_LIMIT);
        SseTimedOutEvent timedOutEvent = new SseTimedOutEvent(memberId);

        emitter.onCompletion(() -> Events.raise(timedOutEvent));
        emitter.onError(e -> {
            Events.raise(timedOutEvent);
            log.error(e.getMessage());
        });
        emitter.onTimeout(() -> Events.raise(timedOutEvent));

        return emitter;
    }

    public void disconnect(final Long memberId) {
        emitters.remove(memberId);
    }

    /** 특정 회원의 SSE 연결 여부 확인 */
    public boolean isConnected(final Long memberId) {
        return emitters.containsKey(memberId);
    }

    /** 범용 알림 전송 (NotificationSender용) title과 message를 직접 받아서 전송 */
    public void sendNotification(final Long memberId, final String title, final String message) {
        SseEmitter emitter = emitters.get(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(Map.of(
                                "title", title,
                                "message", message)));
                log.debug("범용 알림 전송 성공 - memberId: {}", memberId);
            } catch (IOException e) {
                log.warn("범용 알림 전송 실패 - memberId: {}", memberId);
                emitter.completeWithError(e);
                emitters.remove(memberId);
            }
        }
    }
}
