package com.backend.allreva.notification.query.model;

import com.backend.allreva.notification.domain.NotificationType;

public record NotificationResult(
        Long id,
        NotificationType type,
        String title,
        String message,
        Long recipientId,
        Long senderId,
        Long resourceId,
        String resourceName,
        boolean read) {}
