package com.backend.allreva.module.recruitment.chat.exception;

import org.springframework.http.HttpStatus;

import com.backend.allreva.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChattingErrorCode implements ErrorCode {

    GROUP_CHAT_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "GROUP_CHAT_NOT_FOUND", "해당 단체 채팅방을 찾을 수 없습니다."),
    CHAT_ROOM_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "CHAT_ROOM_NOT_FOUND", "해당 채팅방을 찾을 수 없습니다."),
    PARTICIPANT_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "PARTICIPANT_NOT_FOUND", "해당 참여자를 찾을 수 없습니다."),
    INVALID_MANAGER(HttpStatus.FORBIDDEN.value(), "INVALID_MANAGER", "채팅방 수정 권한이 없습니다."),
    DO_NOT_MEET_CONDITIONS_TO_DELETE(HttpStatus.BAD_REQUEST.value(), "DO_NOT_MEET_CONDITION_TO_DELETE",
            "방장 혼자 남은 경우만 삭제가 가능합니다.");

    private final int status;
    private final String code;
    private final String message;

}
