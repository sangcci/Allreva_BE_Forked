package com.backend.allreva.module.notification.infra.fcm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.notification.application.port.NotificationSender;
import com.backend.allreva.module.notification.exception.NotificationErrorCode;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmSender implements NotificationSender {

    private final FcmClient fcmClient;

    @Value("${fcm.project-id}")
    private String projectId;

    @Override
    public void sendMessage(
            final String target,
            final String title,
            final String message) {
        try {
            String accessToken = FcmTokenUtils.getAccessToken();
            String authorizationHeader = "Bearer " + accessToken;
            FcmMessage fcmMessage = FcmMessage.from(target, false, title, message);

            fcmClient.sendMessage(
                    authorizationHeader,
                    fcmMessage,
                    projectId);

            log.debug("FCM 메시지 전송 성공 - title: {}", title);
        } catch (CustomException e) {
            // FcmTokenUtils에서 발생한 토큰 발급 실패
            log.warn("FCM 토큰 발급 실패로 메시지 전송 불가 - title: {}", title);
            throw e;
        } catch (FeignException e) {
            // FCM API 호출 실패
            log.error("FCM API 호출 실패 - status: {}, title: {}", e.status(), title, e);
            throw new CustomException(NotificationErrorCode.FCM_SEND_FAILED, e);
        }
    }
}
