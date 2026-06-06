package com.backend.allreva.notification.fcm;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.notification.command.implementation.NotificationDeliveryTargetType;
import com.backend.allreva.notification.command.implementation.NotificationSender;
import com.backend.allreva.notification.domain.NotificationErrorCode;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmSender implements NotificationSender {

    private final FcmClient fcmClient;
    private final FcmTokenUtils fcmTokenUtils;
    private final FcmProperties fcmProperties;

    @Override
    public NotificationDeliveryTargetType targetType() {
        return NotificationDeliveryTargetType.DEVICE_TOKEN;
    }

    @Override
    public void sendMessage(final String target, final String title, final String message) {
        try {
            String accessToken = fcmTokenUtils.getAccessToken();
            String authorizationHeader = "Bearer " + accessToken;
            FcmMessage fcmMessage = FcmMessage.from(target, false, title, message);

            fcmClient.sendMessage(authorizationHeader, fcmMessage, fcmProperties.projectId());

            log.debug("FCM message sent successfully - title: {}", title);
        } catch (CustomException e) {
            log.warn("FCM message send failed due to token issue - title: {}", title);
            throw e;
        } catch (FeignException e) {
            log.error("FCM API call failed - status: {}, title: {}", e.status(), title, e);
            throw new CustomException(NotificationErrorCode.FCM_SEND_FAILED, e);
        }
    }
}
