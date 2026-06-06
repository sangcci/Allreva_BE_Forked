package com.backend.allreva.notification;

import static com.backend.allreva.notification.QNotificationEntity.notificationEntity;

import com.backend.allreva.notification.query.model.NotificationResult;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationQueryDslFinder {

    private final JPAQueryFactory queryFactory;

    // 알림 최신 순 페이징 조회
    public List<NotificationResult> findNotificationsByRecipientId(
            final Long recipientId, final Long lastId, final int pageSize) {
        return queryFactory
                .selectFrom(notificationEntity)
                .where(notificationEntity.recipientId.eq(recipientId), pagingCondition(lastId))
                .orderBy(notificationEntity.createdAt.desc())
                .limit(pageSize)
                .fetch()
                .stream()
                .map(this::toResult)
                .toList();
    }

    private NotificationResult toResult(final NotificationEntity entity) {
        return new NotificationResult(
                entity.getId(),
                entity.getType(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getRecipientId(),
                entity.getSenderId(),
                entity.getResourceId(),
                entity.getResourceName(),
                entity.isRead());
    }

    private BooleanExpression pagingCondition(Long lastId) {
        return lastId != null ? notificationEntity.id.lt(lastId) : null;
    }
}
