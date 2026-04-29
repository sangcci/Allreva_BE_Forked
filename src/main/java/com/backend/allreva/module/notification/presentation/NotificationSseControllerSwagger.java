package com.backend.allreva.module.notification.presentation;

import com.backend.allreva.module.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "알림 SSE API", description = "알림 SSE 구독 관련 API")
public interface NotificationSseControllerSwagger {

    @SecurityRequirement(name = "USER")
    @Operation(summary = "SSE 구독", description = "**[회원]** 알림 수신을 위한 SSE 연결 수립")
    SseEmitter subscribe(Member member);
}
