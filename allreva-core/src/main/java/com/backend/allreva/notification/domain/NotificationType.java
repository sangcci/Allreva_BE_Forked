package com.backend.allreva.notification.domain;

public enum NotificationType {
    RENT_PARTICIPANT_JOINED("차량 대절 참여"),
    SURVEY_PARTICIPANT_JOINED("수요 조사 참여");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
