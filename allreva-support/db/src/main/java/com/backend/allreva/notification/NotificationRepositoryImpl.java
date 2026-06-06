package com.backend.allreva.notification;

import com.backend.allreva.notification.domain.Notification;
import com.backend.allreva.notification.domain.NotificationRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationJpaRepository notificationJpaRepository;
    private final NotificationJdbcRepository notificationJdbcRepository;

    @Override
    public Optional<Notification> findById(final Long id) {
        return notificationJpaRepository.findById(id).map(NotificationEntity::toDomain);
    }

    @Override
    public Optional<Notification> findByIdAndRecipientId(final Long id, final Long recipientId) {
        return notificationJpaRepository.findByIdAndRecipientId(id, recipientId).map(NotificationEntity::toDomain);
    }

    @Override
    public void save(final Notification notification) {
        notificationJpaRepository.save(NotificationEntity.from(notification));
    }

    @Override
    public void saveAll(final List<Notification> notifications) {
        notificationJdbcRepository.saveAllInBatch(notifications);
    }
}
