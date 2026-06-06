package com.backend.allreva.notification.fcm;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "fcm")
public record FcmProperties(String projectId, String serviceAccountKey) {}
