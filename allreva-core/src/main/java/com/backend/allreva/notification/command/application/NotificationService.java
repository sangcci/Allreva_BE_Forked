package com.backend.allreva.notification.command.application;

import com.backend.allreva.notification.command.implementation.NotificationMarker;
import com.backend.allreva.notification.command.implementation.NotificationReader;
import com.backend.allreva.notification.command.implementation.NotificationTargetWriter;
import com.backend.allreva.notification.command.implementation.NotificationWriter;
import com.backend.allreva.notification.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationReader notificationReader;
    private final NotificationMarker notificationMarker;
    private final NotificationWriter notificationWriter;
    private final NotificationTargetWriter targetWriter;

    public void markAsRead(final Long notificationId, final Long memberId) {
        Notification notification = notificationReader.get(notificationId, memberId);

        notificationMarker.markAsRead(notification);

        notificationWriter.save(notification);
    }

    public void registerTarget(final String target, final Long memberId) {
        targetWriter.save(memberId, target);
    }

    public void deleteTarget(final Long memberId) {
        targetWriter.delete(memberId);
    }
}
