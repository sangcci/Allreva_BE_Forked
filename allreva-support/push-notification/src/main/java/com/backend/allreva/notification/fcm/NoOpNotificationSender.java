package com.backend.allreva.notification.fcm;

import com.backend.allreva.notification.command.implementation.NotificationDeliveryTargetType;
import com.backend.allreva.notification.command.implementation.NotificationSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("local | test")
public class NoOpNotificationSender implements NotificationSender {

    @Override
    public NotificationDeliveryTargetType targetType() {
        return NotificationDeliveryTargetType.DEVICE_TOKEN;
    }

    @Override
    public void sendMessage(final String target, final String title, final String message) {
        log.debug("Skip FCM message send on local/test profile - target: {}, title: {}", target, title);
    }
}
