package com.backend.allreva.notification.query.implementation;

import com.backend.allreva.notification.query.model.NotificationResult;
import java.util.List;

public interface NotificationFinderPort {

    List<NotificationResult> findNotificationsByRecipientId(Long recipientId, Long lastId, int pageSize);
}
