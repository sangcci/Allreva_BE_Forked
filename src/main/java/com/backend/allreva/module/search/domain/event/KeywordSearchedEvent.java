package com.backend.allreva.module.search.domain.event;

import com.backend.allreva.events.Event;
import lombok.Getter;

@Getter
public class KeywordSearchedEvent extends Event {

    private final String keyword;

    public KeywordSearchedEvent(final String keyword) {
        this.keyword = keyword;
    }
}
