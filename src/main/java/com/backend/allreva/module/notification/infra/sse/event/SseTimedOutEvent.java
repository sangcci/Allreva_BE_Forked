package com.backend.allreva.module.notification.infra.sse.event;

import com.backend.allreva.common.event.Event;
import lombok.Getter;

@Getter
public class SseTimedOutEvent extends Event {

    private final Long memberId;

    public SseTimedOutEvent(final Long memberId) {
        this.memberId = memberId;
    }
}
