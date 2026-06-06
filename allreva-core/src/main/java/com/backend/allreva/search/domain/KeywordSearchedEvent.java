package com.backend.allreva.search.domain;

import com.backend.allreva.common.event.Event;
import lombok.Getter;

@Getter
public class KeywordSearchedEvent extends Event {

    private final String keyword;

    public KeywordSearchedEvent(final String keyword) {
        this.keyword = keyword;
    }
}
