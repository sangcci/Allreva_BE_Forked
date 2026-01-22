package com.backend.allreva.module.notification.application.dto;

import lombok.Builder;

@Builder
public record NotificationTargetRequest(
        String target) {

}
