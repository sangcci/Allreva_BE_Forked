package com.backend.allreva.module.notification.domain;

/**
 * 알림 타입
 * 각 모듈에서 발생하는 알림의 종류를 정의
 */
public enum NotificationType {

    // 채팅 관련 알림
    CHAT_MESSAGE("채팅 메시지"),
    CHAT_MEMBER_JOINED("채팅방 입장"),
    CHAT_MEMBER_LEFT("채팅방 퇴장"),

    // 차량 대절 관련 알림
    RENT_REGISTERED("차량 대절 등록"),
    RENT_PARTICIPANT_JOINED("차량 대절 참여"),
    RENT_CANCELLED("차량 대절 취소"),

    // 수요 조사 관련 알림
    SURVEY_REGISTERED("수요 조사 등록"),
    SURVEY_RESPONSE_RECEIVED("수요 조사 응답"),
    SURVEY_CLOSED("수요 조사 마감"),

    // 콘서트 관련 알림
    CONCERT_REMINDER("콘서트 리마인더"),
    CONCERT_UPDATED("콘서트 정보 변경"),

    // 일기 관련 알림
    DIARY_LIKE_RECEIVED("일기 좋아요"),
    DIARY_COMMENT_RECEIVED("일기 댓글"),

    // 일반 알림
    GENERAL("일반 알림");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
