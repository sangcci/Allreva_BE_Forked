package com.backend.allreva.common.event;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventsConfig {

    private final ApplicationEventPublisher publisher;

    public EventsConfig(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Bean
    public InitializingBean eventsInitializer() {
        return () -> Events.setPublisher(publisher);
    }
}
