package com.backend.allreva.module.notification.domain;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    Optional<Notification> findById(Long id);

    Optional<Notification> findByIdAndRecipientId(Long id, Long recipientId);

    List<Notification> findNotificationsByRecipientId(Long recipientId, Long lastId, int pageSize);

    List<Notification> findAll();

    void saveAll(List<Notification> notifications);
}
