package com.backend.allreva.notification.command.implementation;

public interface NotificationSender {

    default NotificationDeliveryTargetType targetType() {
        return NotificationDeliveryTargetType.MEMBER_ID;
    }

    void sendMessage(String target, String title, String message);
}
