package com.backend.allreva.notification.fcm;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!local & !test")
@EnableFeignClients(clients = FcmClient.class)
@EnableConfigurationProperties(FcmProperties.class)
public class FcmPushNotificationConfig {}
