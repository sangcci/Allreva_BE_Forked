package com.backend.allreva.notification;

import com.backend.allreva.common.persistence.BaseEntity;
import com.backend.allreva.notification.domain.Notification;
import com.backend.allreva.notification.domain.NotificationType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "Notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String title;
    private String message;
    private Long recipientId;
    private Long senderId;

    private Long resourceId;
    private String resourceName;

    private boolean isRead;

    private NotificationEntity(
            final Long id,
            final NotificationType type,
            final String title,
            final String message,
            final Long recipientId,
            final Long senderId,
            final Long resourceId,
            final String resourceName,
            final boolean isRead) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.message = message;
        this.recipientId = recipientId;
        this.senderId = senderId;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.isRead = isRead;
    }

    public static NotificationEntity from(final Notification notification) {
        return new NotificationEntity(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getRecipientId(),
                notification.getSenderId(),
                notification.getResourceId(),
                notification.getResourceName(),
                notification.isRead());
    }

    public Notification toDomain() {
        return Notification.builder()
                .id(id)
                .type(type)
                .title(title)
                .message(message)
                .recipientId(recipientId)
                .senderId(senderId)
                .resourceId(resourceId)
                .resourceName(resourceName)
                .read(isRead)
                .build();
    }
}
