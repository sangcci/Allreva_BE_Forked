package com.backend.allreva.notification;

import com.backend.allreva.notification.query.implementation.NotificationFinderPort;
import com.backend.allreva.notification.query.model.NotificationResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationFinderAdapter implements NotificationFinderPort {

    private final NotificationQueryDslFinder notificationQueryDslFinder;

    @Override
    public List<NotificationResult> findNotificationsByRecipientId(
            final Long recipientId, final Long lastId, final int pageSize) {
        return notificationQueryDslFinder.findNotificationsByRecipientId(recipientId, lastId, pageSize);
    }
}
