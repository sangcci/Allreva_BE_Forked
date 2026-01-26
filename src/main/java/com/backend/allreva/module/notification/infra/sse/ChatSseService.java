package com.backend.allreva.module.notification.infra.sse;

import com.backend.allreva.module.chat.domain.participant.value.ChatType;
import com.backend.allreva.module.chat.domain.participant.value.PreviewMessage;
import com.backend.allreva.module.notification.infra.sse.event.SseConnectedEvent;
import com.backend.allreva.common.event.Events;
import com.backend.allreva.module.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@Service
public class ChatSseService {

    private final SseConnectionManager sseConnectionManager;

    public SseEmitter connect(final Long memberId) {
        Events.raise(new SseConnectedEvent(memberId));
        return sseConnectionManager.connectByMemberId(memberId);
    }

    public void sendChatNotification(
            final Long chatId,
            final ChatType chatType,
            final PreviewMessage previewMessage,
            final Member member
            ) {
        sseConnectionManager.sendNotification(chatId, chatType, previewMessage, member);
    }

}
