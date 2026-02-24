package com.backend.allreva.module.member.domain.event;

import com.backend.allreva.common.event.Event;
import lombok.Getter;

@Getter
public class MemberRegisteredEvent extends Event {

    private final Long memberId;

    public MemberRegisteredEvent(Long memberId) {
        this.memberId = memberId;
    }
}
