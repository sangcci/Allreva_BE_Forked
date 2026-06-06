package com.backend.allreva.notification.command.implementation;

import com.backend.allreva.notification.domain.Notification;
import com.backend.allreva.notification.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationWriter {

    private final NotificationRepository notificationRepository;

    public void save(final Notification notification) {
        notificationRepository.save(notification);
    }
}
