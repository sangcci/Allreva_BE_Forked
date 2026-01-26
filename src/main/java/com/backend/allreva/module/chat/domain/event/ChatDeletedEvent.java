package com.backend.allreva.module.chat.domain.event;

import com.backend.allreva.common.event.Event;
import lombok.Getter;

@Getter
public class ChatDeletedEvent extends Event {

    private final Long groupChatId;
    private final Long memberId;

    public ChatDeletedEvent(
            final Long groupChatId,
            final Long memberId
    ) {
        this.groupChatId = groupChatId;
        this.memberId = memberId;
    }
}
