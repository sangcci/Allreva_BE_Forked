package com.backend.allreva.support;

import com.backend.allreva.notification.fcm.FcmPushNotificationConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = FcmPushNotificationConfig.class)
@ImportAutoConfiguration({FeignAutoConfiguration.class, JacksonAutoConfiguration.class})
public abstract class PushNotificationTestSupport {

    @DynamicPropertySource
    static void configureProperties(final DynamicPropertyRegistry registry) {
        registry.add("fcm.project-id", () -> "test-fcm-project-id");
        registry.add("fcm.service-account-key", () -> "classpath:firebase/firebase-service-account-key.json");
    }
}
