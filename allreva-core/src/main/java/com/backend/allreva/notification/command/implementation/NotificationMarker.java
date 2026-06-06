package com.backend.allreva.notification.command.implementation;

import com.backend.allreva.notification.domain.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMarker {

    public void markAsRead(final Notification notification) {
        notification.read();
    }
}
