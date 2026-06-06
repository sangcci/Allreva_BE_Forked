package com.backend.allreva.notification.domain;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    Optional<Notification> findById(Long id);

    Optional<Notification> findByIdAndRecipientId(Long id, Long recipientId);

    void save(Notification notification);

    void saveAll(List<Notification> notifications);
}
