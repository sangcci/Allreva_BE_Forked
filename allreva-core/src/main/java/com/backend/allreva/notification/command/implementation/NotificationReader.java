package com.backend.allreva.notification.command.implementation;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.notification.domain.Notification;
import com.backend.allreva.notification.domain.NotificationErrorCode;
import com.backend.allreva.notification.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationReader {

    private final NotificationRepository notificationRepository;

    public Notification get(final Long id, final Long recipientId) {
        return notificationRepository
                .findByIdAndRecipientId(id, recipientId)
                .orElseThrow(() -> new CustomException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));
    }
}
