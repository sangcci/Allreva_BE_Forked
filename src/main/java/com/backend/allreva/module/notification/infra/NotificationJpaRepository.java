package com.backend.allreva.module.notification.infra;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.allreva.module.notification.domain.Notification;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findByIdAndRecipientId(Long id, Long recipientId);

    List<Notification> findNotificationsByRecipientId(Long recipientId);
}
