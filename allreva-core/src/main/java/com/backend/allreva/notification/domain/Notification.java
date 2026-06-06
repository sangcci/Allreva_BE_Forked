package com.backend.allreva.notification.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Notification {

    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private Long recipientId;
    private Long senderId;
    private Long resourceId;
    private String resourceName;
    private boolean read;

    @Builder
    private Notification(
            final Long id,
            final NotificationType type,
            final String title,
            final String message,
            final Long recipientId,
            final Long senderId,
            final Long resourceId,
            final String resourceName,
            final boolean read) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.message = message;
        this.recipientId = recipientId;
        this.senderId = senderId;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.read = read;
    }

    public void read() {
        this.read = true;
    }

    public boolean isRead() {
        return read;
    }
}
