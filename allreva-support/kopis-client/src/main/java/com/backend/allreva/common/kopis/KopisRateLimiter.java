package com.backend.allreva.common.kopis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KopisRateLimiter {

    private static final long REQUEST_INTERVAL_MILLIS = 100;

    public void acquire() {
        try {
            Thread.sleep(REQUEST_INTERVAL_MILLIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("KOPIS request interrupted", e);
        }
    }
}
