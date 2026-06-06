package com.backend.allreva.notification;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, Long> {

    Optional<NotificationEntity> findByIdAndRecipientId(Long id, Long recipientId);
}
