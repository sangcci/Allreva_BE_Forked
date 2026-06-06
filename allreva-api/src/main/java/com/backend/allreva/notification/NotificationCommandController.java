package com.backend.allreva.notification;

import com.backend.allreva.auth.security.AuthMember;
import com.backend.allreva.common.web.response.View;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.notification.command.application.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationCommandController implements NotificationCommandControllerSwagger {

    private final NotificationService notificationCommandService;

    @Override
    @PatchMapping("/read")
    public View<Void> markAsRead(@RequestParam final Long notificationId, @AuthMember final Member member) {
        notificationCommandService.markAsRead(notificationId, member.getId());
        return View.onSuccess();
    }

    @Override
    @PostMapping("/device-token")
    public View<Void> registerDeviceToken(@RequestParam final String target, @AuthMember final Member member) {
        notificationCommandService.registerTarget(target, member.getId());
        return View.onSuccess();
    }

    @Override
    @DeleteMapping("/device-token")
    public View<Void> deleteDeviceToken(@AuthMember final Member member) {
        notificationCommandService.deleteTarget(member.getId());
        return View.onSuccess();
    }
}
