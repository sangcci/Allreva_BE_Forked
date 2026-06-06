package com.backend.allreva.notification.fcm;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.backend.allreva.notification.command.implementation.NotificationDeliveryTargetType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@SuppressWarnings("NonAsciiCharacters")
@ExtendWith(MockitoExtension.class)
@DisplayName("FcmSender 단위 테스트")
class FcmSenderTest {

    @Mock
    private FcmClient fcmClient;

    @Mock
    private FcmTokenUtils fcmTokenUtils;

    @Captor
    private ArgumentCaptor<FcmMessage> fcmMessageCaptor;

    @Nested
    @DisplayName("targetType 메서드는")
    class Describe_targetType {

        @Test
        void DEVICE_TOKEN을_반환한다() {
            FcmSender sender =
                    new FcmSender(fcmClient, fcmTokenUtils, new FcmProperties("project-id", "classpath:key.json"));

            NotificationDeliveryTargetType result = sender.targetType();

            assertThat(result).isEqualTo(NotificationDeliveryTargetType.DEVICE_TOKEN);
        }
    }

    @Nested
    @DisplayName("sendMessage 메서드는")
    class Describe_sendMessage {

        @Test
        void FCM_메시지를_프로젝트_아이디와_인증_헤더로_전송한다() {
            given(fcmTokenUtils.getAccessToken()).willReturn("access-token");
            FcmSender sender =
                    new FcmSender(fcmClient, fcmTokenUtils, new FcmProperties("project-id", "classpath:key.json"));

            sender.sendMessage("device-token", "알림 제목", "알림 본문");

            then(fcmClient)
                    .should()
                    .sendMessage(eq("Bearer access-token"), fcmMessageCaptor.capture(), eq("project-id"));
            FcmMessage result = fcmMessageCaptor.getValue();
            assertThat(result.validateOnly()).isFalse();
            assertThat(result.message().token()).isEqualTo("device-token");
            assertThat(result.message().notification().title()).isEqualTo("알림 제목");
            assertThat(result.message().notification().body()).isEqualTo("알림 본문");
        }
    }
}
