package com.backend.allreva.module.notification.domain;

import com.backend.allreva.common.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 알림 타입
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    // 기본 메시지 정보
    private String title;
    private String message;

    // 수신자 정보
    private Long recipientId;

    // 발신자 정보 (선택적)
    private Long senderId;
    private String senderName;

    // 방/룸 정보 (선택적)
    private Long roomId;
    private String roomName;

    // 읽음 여부
    private boolean isRead;

    @Builder
    private Notification(
            NotificationType type,
            String title,
            String message,
            Long recipientId,
            Long senderId,
            String senderName,
            Long roomId,
            String roomName) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.recipientId = recipientId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.roomId = roomId;
        this.roomName = roomName;
        this.isRead = false;
    }

    /**
     * NotificationEvent로부터 Notification 생성
     */
    public static Notification fromEvent(NotificationEvent event, Long recipientId, String title, String message) {
        return Notification.builder()
                .type(event.getType())
                .title(title)
                .message(message)
                .recipientId(recipientId)
                .senderId(event.getSenderId())
                .senderName(event.getSenderName())
                .roomId(event.getRoomId())
                .roomName(event.getRoomName())
                .build();
    }

    public void read() {
        this.isRead = true;
    }
}
