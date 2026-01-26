package com.backend.allreva.module.notification.infra.sse.event;

import com.backend.allreva.common.event.Event;
import lombok.Getter;

@Getter
public class SseConnectedEvent extends Event {

    private final Long memberId;

    public SseConnectedEvent(final Long memberId) {
        this.memberId = memberId;
    }
}
