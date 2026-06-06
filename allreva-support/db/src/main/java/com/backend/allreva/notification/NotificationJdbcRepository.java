package com.backend.allreva.notification;

import com.backend.allreva.notification.domain.Notification;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void saveAllInBatch(List<Notification> notifications) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO notification (type, title, message, recipient_id, sender_id, resource_id, resource_name, is_read, created_at, updated_at, deleted_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                notifications,
                notifications.size(),
                (ps, notification) -> {
                    ps.setString(1, notification.getType().name());
                    ps.setString(2, notification.getTitle());
                    ps.setString(3, notification.getMessage());
                    ps.setLong(4, notification.getRecipientId());
                    if (notification.getSenderId() != null) {
                        ps.setLong(5, notification.getSenderId());
                    } else {
                        ps.setNull(5, Types.BIGINT);
                    }
                    if (notification.getResourceId() != null) {
                        ps.setLong(6, notification.getResourceId());
                    } else {
                        ps.setNull(6, Types.BIGINT);
                    }
                    ps.setString(7, notification.getResourceName());
                    ps.setBoolean(8, false);
                    LocalDateTime now = LocalDateTime.now();
                    ps.setObject(9, now);
                    ps.setObject(10, now);
                    ps.setNull(11, Types.TIMESTAMP);
                });
    }
}
