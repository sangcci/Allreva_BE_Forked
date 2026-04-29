package com.backend.allreva.module.notification.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.notification.application.dto.NotificationIdRequest;
import com.backend.allreva.module.notification.application.dto.NotificationTargetRequest;
import com.backend.allreva.module.notification.domain.Notification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(name = "알림 API", description = "알림 관련 API")
public interface NotificationControllerSwagger {

    @SecurityRequirement(name = "USER")
    @Operation(summary = "알림 목록 조회", description = "**[회원]** 무한 스크롤. lastId 미전달 시 첫 페이지 조회")
    Response<List<Notification>> getNotifications(Member member, Long lastId, int pageSize);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "알림 읽음 처리", description = "**[회원]**")
    Response<Void> markAsRead(Member member, NotificationIdRequest notificationIdRequest);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "디바이스 토큰 등록", description = "**[회원]**")
    Response<Void> registerDeviceToken(Member member, NotificationTargetRequest deviceTokenRequest);

    @SecurityRequirement(name = "USER")
    @Operation(summary = "디바이스 토큰 삭제", description = "**[회원]**")
    Response<Void> deleteDeviceToken(Member member);
}
