package com.backend.allreva.module.recruitment.chat.presentation;

import java.util.List;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.recruitment.chat.application.dto.EnterChatResponse;
import com.backend.allreva.module.recruitment.chat.application.dto.MessageResponse;
import com.backend.allreva.module.member.domain.Member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "메시지 API", description = "채팅 메시지 조회 API")
public interface MessageControllerSwagger {

    @Operation(summary = "그룹 채팅 입장", description = "그룹 채팅방 입장 시 기본 메시지 조회")
    Response<EnterChatResponse> enterGroupChat(Long groupChatId, Member member);

    @Operation(summary = "읽은 메시지 조회", description = "기준 번호 이전의 읽은 메시지 목록 조회")
    Response<List<MessageResponse>> findReadGroupMessages(Long groupChatId, long criteriaNumber);

    @Operation(summary = "안읽은 메시지 조회", description = "기준 번호 이후의 안읽은 메시지 목록 조회")
    Response<List<MessageResponse>> findUnreadGroupMessages(Long groupChatId, long criteriaNumber);
}
