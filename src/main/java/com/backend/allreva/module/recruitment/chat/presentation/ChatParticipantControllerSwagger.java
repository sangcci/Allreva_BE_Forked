package com.backend.allreva.module.recruitment.chat.presentation;

import java.util.SortedSet;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.recruitment.chat.domain.participant.value.ChatSummary;
import com.backend.allreva.module.member.domain.Member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "채팅 참여자 API", description = "채팅 참여자 API")
public interface ChatParticipantControllerSwagger {

    @Operation(summary = "참여 중인 채팅 목록 조회", description = "로그인된 사용자가 참여 중인 채팅방 목록 조회")
    Response<SortedSet<ChatSummary>> findParticipatingChats(Member member);
}
