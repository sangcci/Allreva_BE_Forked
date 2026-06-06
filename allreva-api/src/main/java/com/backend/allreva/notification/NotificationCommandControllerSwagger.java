package com.backend.allreva.notification;

import com.backend.allreva.common.web.response.View;
import com.backend.allreva.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "알림 API", description = "알림 Command API")
public interface NotificationCommandControllerSwagger {

    @SecurityRequirement(name = "USER")
    @Operation(summary = "알림 읽음 처리", description = "**[회원]**")
    View<Void> markAsRead(Long notificationId, Member member);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "디바이스 토큰 등록", description = "**[회원]**")
    View<Void> registerDeviceToken(String target, Member member);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "디바이스 토큰 삭제", description = "**[회원]**")
    View<Void> deleteDeviceToken(Member member);
}
