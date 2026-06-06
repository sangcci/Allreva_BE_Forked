package com.backend.allreva.notification.sse;

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

    public static final String SSE_NAME = "notification";
    public static final String INIT_MESSAGE = "SSE Connected";

    public static final Long TIME_LIMIT = 60000 * 60 * 24L;

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter connect(final Long memberId) {
        SseEmitter emitter = createSseEmitter(memberId);
        emitters.put(memberId, emitter);

        try {
            emitter.send(SseEmitter.event().name(SSE_NAME).data(INIT_MESSAGE));
        } catch (IOException e) {
            log.error("SSE 초기 연결 메시지 전송 실패 - memberId: {}", memberId);
            emitters.remove(memberId);
        }

        return emitter;
    }

    private SseEmitter createSseEmitter(final Long memberId) {
        SseEmitter emitter = new SseEmitter(TIME_LIMIT);

        emitter.onCompletion(() -> disconnect(memberId));
        emitter.onError(e -> {
            log.error("SSE 에러 발생 - memberId: {}, message: {}", memberId, e.getMessage());
            disconnect(memberId);
        });
        emitter.onTimeout(() -> disconnect(memberId));

        return emitter;
    }

    public void disconnect(final Long memberId) {
        emitters.remove(memberId);
        log.debug("SSE 연결 종료 - memberId: {}", memberId);
    }

    public boolean isConnected(final Long memberId) {
        return emitters.containsKey(memberId);
    }

    public void sendNotification(final Long memberId, final String title, final String message) {
        SseEmitter emitter = emitters.get(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name(SSE_NAME)
                        .data(Map.of(
                                "title", title,
                                "message", message)));
                log.debug("SSE 알림 전송 성공 - memberId: {}", memberId);
            } catch (IOException e) {
                log.warn("SSE 알림 전송 실패 - memberId: {}", memberId);
                emitter.completeWithError(e);
                disconnect(memberId);
            }
        }
    }
}
