package com.backend.allreva.module.notification.infra.sse;

import com.backend.allreva.module.recruitment.chat.domain.participant.value.ChatType;
import com.backend.allreva.module.recruitment.chat.domain.participant.value.Participant;
import com.backend.allreva.module.recruitment.chat.domain.participant.value.PreviewMessage;
import lombok.Getter;

@Getter
public class ChatPreviewResponse {

    private final Long chatId;
    private final ChatType chatType;
    private final PreviewMessage previewMessage;
    private final Participant participant;

    public ChatPreviewResponse(
            final Long chatId,
            final ChatType chatType,
            final PreviewMessage previewMessage,
            final Participant participant
    ) {
        this.chatId = chatId;
        this.chatType = chatType;
        this.previewMessage = previewMessage;
        this.participant = participant;
    }
}
