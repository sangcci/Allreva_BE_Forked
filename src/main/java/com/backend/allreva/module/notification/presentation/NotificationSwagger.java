package com.backend.allreva.module.notification.presentation;

import java.util.List;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.notification.application.dto.NotificationTargetRequest;
import com.backend.allreva.module.notification.application.dto.NotificationIdRequest;
import com.backend.allreva.module.notification.domain.Notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "알림 조회 API")
public interface NotificationSwagger {

    @Operation(summary = "사용자 알림 조회", description = """
            알림 종류를 type에 입력해주세요. default값은 ALL입니다. lastId와 lastEndDate는 무한 스크롤을 위한 값입니다.
                """)
    Response<List<Notification>> getNotifications(
            Member member,
            Long lastId,
            int pageSize);

    @Operation(summary = "사용자 알림 읽음 표시", description = "알림을 클릭했을 경우 읽음 상태로 전환되는 API 입니다. 알림 ID로 알림을 찾아 읽음 표시로 변경됩니다.")
    Response<Void> markAsRead(
            Member member,
            NotificationIdRequest notificationIdRequest);

    @Operation(summary = "디바이스 토큰 등록")
    Response<Void> registerDeviceToken(
            Member member,
            NotificationTargetRequest deviceTokenRequest);

    @Operation(summary = "디바이스 토큰 삭제")
    Response<Void> deleteDeviceToken(
            Member member);
}
