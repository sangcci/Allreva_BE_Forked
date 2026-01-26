package com.backend.allreva.module.chat.domain.event;

import com.backend.allreva.common.event.Event;
import lombok.Getter;

@Getter
public class ChatCreatedEvent extends Event {

    private Long groupChatId;
    private Long memberId;

    public ChatCreatedEvent(
            final Long groupChatId,
            final Long memberId
    ) {
        this.groupChatId = groupChatId;
        this.memberId = memberId;
    }
}
