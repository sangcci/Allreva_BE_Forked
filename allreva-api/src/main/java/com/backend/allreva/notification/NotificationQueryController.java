package com.backend.allreva.notification;

import com.backend.allreva.auth.security.AuthMember;
import com.backend.allreva.common.web.response.View;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.notification.query.application.NotificationFinder;
import com.backend.allreva.notification.query.model.NotificationResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationQueryController implements NotificationQueryControllerSwagger {

    private final NotificationFinder notificationQueryService;

    @Override
    @GetMapping
    public View<List<NotificationResult>> getNotifications(
            @AuthMember final Member member,
            @RequestParam(required = false) final Long lastId,
            @RequestParam(defaultValue = "10") final int pageSize) {
        return View.onSuccess(notificationQueryService.getNotificationsByRecipientId(member, lastId, pageSize));
    }
}
