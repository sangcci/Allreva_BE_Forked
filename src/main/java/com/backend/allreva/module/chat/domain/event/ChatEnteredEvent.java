package com.backend.allreva.module.chat.domain.event;

import com.backend.allreva.module.chat.domain.participant.value.ChatType;
import com.backend.allreva.common.event.Event;
import lombok.Getter;

@Getter
public class ChatEnteredEvent extends Event {

    private final Long chatId;
    private final ChatType chatType;

    private final Long memberId;

    private final Long lastReadMessageNumber;

    public ChatEnteredEvent(
            final Long chatId,
            final ChatType chatType,
            final Long memberId,
            final Long lastReadMessageNumber
    ) {
        this.chatId = chatId;
        this.chatType = chatType;
        this.memberId = memberId;
        this.lastReadMessageNumber = lastReadMessageNumber;
    }
}
