package com.backend.allreva.module.recruitment.chat.domain.event;

import com.backend.allreva.common.event.Event;
import lombok.Getter;

@Getter
public class ChatMemberLeftEvent extends Event {

    private final Long memberId;
    private final Long groupChatId;

    public ChatMemberLeftEvent(
            final Long memberId,
            final Long groupChatId
    ) {
        this.memberId = memberId;
        this.groupChatId = groupChatId;
    }
}
