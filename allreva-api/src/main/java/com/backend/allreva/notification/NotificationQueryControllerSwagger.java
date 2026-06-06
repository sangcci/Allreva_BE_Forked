package com.backend.allreva.notification;

import com.backend.allreva.common.web.response.View;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.notification.query.model.NotificationResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(name = "알림 API", description = "알림 Query API")
public interface NotificationQueryControllerSwagger {

    @SecurityRequirement(name = "USER")
    @Operation(summary = "알림 목록 조회", description = "**[회원]** 무한 스크롤. lastId 미전달 시 첫 페이지 조회")
    View<List<NotificationResult>> getNotifications(Member member, Long lastId, int pageSize);
}
