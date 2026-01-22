package com.backend.allreva.module.notification.infra;

import static com.backend.allreva.module.notification.domain.QNotification.notification;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.backend.allreva.module.notification.domain.Notification;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationDslRepository {

    private final JPAQueryFactory queryFactory;

    // 알림 최신 순 페이징 조회
    public List<Notification> findNotificationsByRecipientId(
            final Long recipientId,
            final Long lastId,
            final int pageSize) {
        return queryFactory
                .selectFrom(notification)
                .where(
                        notification.recipientId.eq(recipientId),
                        pagingCondition(lastId))
                .orderBy(notification.createdAt.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression pagingCondition(Long lastId) {
        return lastId != null ? notification.id.lt(lastId) : null;
    }
}
