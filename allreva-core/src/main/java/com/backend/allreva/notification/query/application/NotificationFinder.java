package com.backend.allreva.notification.query.application;

import com.backend.allreva.member.domain.Member;
import com.backend.allreva.notification.query.implementation.NotificationFinderPort;
import com.backend.allreva.notification.query.model.NotificationResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationFinder {

    private final NotificationFinderPort notificationFinderPort;

    @Transactional(readOnly = true)
    public List<NotificationResult> getNotificationsByRecipientId(
            final Member member, final Long lastId, final int pageSize) {
        return notificationFinderPort.findNotificationsByRecipientId(member.getId(), lastId, pageSize);
    }
}
