package com.backend.allreva.module.notification.infra.sse;

import com.backend.allreva.module.recruitment.chat.domain.MemberGroupChatRepository;
import com.backend.allreva.module.recruitment.chat.domain.participant.value.ChatType;
import com.backend.allreva.module.recruitment.chat.domain.participant.value.Participant;
import com.backend.allreva.module.recruitment.chat.domain.participant.value.PreviewMessage;
import com.backend.allreva.module.notification.infra.sse.event.SseTimedOutEvent;
import com.backend.allreva.common.event.Events;
import com.backend.allreva.common.model.Image;
import com.backend.allreva.module.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@RequiredArgsConstructor
@Component
public class SseConnectionManager {

    public static final String SSE_NAME = "SSE_PreviewMessage";
    public static final String INIT_MESSAGE = "채팅 SSE 연결";

    public static final Long TIME_LIMIT = 60000 * 60 * 24L;

    private final MemberGroupChatRepository memberGroupChatRepository;

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();


    public SseEmitter connectByMemberId(final Long memberId) {
        SseEmitter emitter = emitters.computeIfAbsent(memberId,
                id -> createSseEmitter(memberId));

        sendInitMessage(emitter);
        return emitter;
    }

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


    public void sendNotification(
            final Long chatId,
            final ChatType chatType,
            final PreviewMessage previewMessage,
            final Member member
    ) {
        Image memberProfile = new Image(member.getMemberInfo().getProfileImageUrl());
        Participant sender = new Participant(
                member.getId(),
                member.getMemberInfo().getNickname(),
                memberProfile
        );

        Set<Long> memberIds = memberGroupChatRepository
                .findAllMemberIdByGroupChatId(chatId);

        ChatPreviewResponse payload = new ChatPreviewResponse(
                chatId,
                chatType,
                previewMessage,
                sender
        );
        sendForEachMembers(memberIds, payload);
    }

    private void sendForEachMembers(
            final Set<Long> memberIds,
            final ChatPreviewResponse payload
    ) {
        memberIds.forEach(memberId -> {
            if (emitters.containsKey(memberId)) {
                SseEmitter emitter = emitters.get(memberId);
                sendPreviewMessageResponse(emitter, payload);
            }
        });
    }

    private void sendInitMessage(
            final SseEmitter emitter
    ) {
        try {
            emitter.send(SseEmitter.event()
                    .name(SSE_NAME)
                    .data(INIT_MESSAGE));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }

    private void sendPreviewMessageResponse(
            final SseEmitter emitter,
            final ChatPreviewResponse payload
    ) {
        try {
            emitter.send(SseEmitter.event()
                    .name(SSE_NAME)
                    .data(payload));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }

    public void disconnect(final Long memberId) {
        emitters.remove(memberId);
    }

    /**
     * 특정 회원의 SSE 연결 여부 확인
     */
    public boolean isConnected(final Long memberId) {
        return emitters.containsKey(memberId);
    }

    /**
     * 범용 알림 전송 (NotificationSender용)
     * title과 message를 직접 받아서 전송
     */
    public void sendNotification(final Long memberId, final String title, final String message) {
        SseEmitter emitter = emitters.get(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(Map.of(
                                "title", title,
                                "message", message
                        )));
                log.debug("범용 알림 전송 성공 - memberId: {}", memberId);
            } catch (IOException e) {
                log.warn("범용 알림 전송 실패 - memberId: {}", memberId);
                emitter.completeWithError(e);
                emitters.remove(memberId);
            }
        }
    }

}
