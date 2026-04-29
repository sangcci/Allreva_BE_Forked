package com.backend.allreva.module.notification.presentation;

import com.backend.allreva.module.auth.security.AuthMember;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.notification.infra.sse.SseConnectionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationSseController implements NotificationSseControllerSwagger {

    private final SseConnectionManager sseConnectionManager;

    @Override
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthMember Member member) {
        return sseConnectionManager.connect(member.getId());
    }
}
