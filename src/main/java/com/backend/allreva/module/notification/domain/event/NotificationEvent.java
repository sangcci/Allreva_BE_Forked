package com.backend.allreva.module.notification.domain.event;

import com.backend.allreva.events.Event;
import com.backend.allreva.module.notification.domain.value.NotificationType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationEvent extends Event {

    // 알림 타입
    private final NotificationType type;

    // 수신자 정보
    private final List<Long> recipientIds;

    // 발신자 정보 (선택적)
    private final Long senderId;
    private final String senderName;

    // 방/룸 정보 (선택적 - 채팅, 대절, 수요조사 등에 사용)
    private final Long roomId;
    private final String roomName;

    // 메시지 내용
    private final String content;

    // 추가 메타데이터 (확장성)
    @Builder.Default
    private final Map<String, Object> metadata = new HashMap<>();
}
