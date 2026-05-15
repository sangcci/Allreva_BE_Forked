package com.backend.allreva.events;

import lombok.Getter;

@Getter
public abstract class Event {

    private final long timestamp;

    protected Event() {
        this.timestamp = System.currentTimeMillis();
    }
}
