package com.backend.allreva.notification.command.implementation;

import com.backend.allreva.notification.domain.Notification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationNotifier {

    private final List<NotificationSender> notificationSenders;

    public boolean notify(final Notification notification, final String target) {
        return notificationSenders.stream()
                .filter(sender -> sender.targetType() == NotificationDeliveryTargetType.DEVICE_TOKEN)
                .map(sender -> send(sender, target, notification))
                .reduce(false, Boolean::logicalOr);
    }

    private boolean send(final NotificationSender sender, final String target, final Notification notification) {
        try {
            sender.sendMessage(target, notification.getTitle(), notification.getMessage());
            return true;
        } catch (Exception e) {
            log.debug("알림 전송 실패. recipientId: {}, target: {}", notification.getRecipientId(), target, e);
            return false;
        }
    }
}
