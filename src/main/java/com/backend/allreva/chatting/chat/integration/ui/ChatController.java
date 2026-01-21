package com.backend.allreva.chatting.chat.integration.ui;

import java.util.SortedSet;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.allreva.chatting.chat.integration.model.ChatParticipantDoc;
import com.backend.allreva.chatting.chat.integration.model.ChatParticipantRepository;
import com.backend.allreva.chatting.chat.integration.model.value.ChatSummary;
import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.member.command.domain.Member;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.member.exception.MemberErrorCode;
import com.backend.allreva.module.auth.security.AuthMember;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
@RestController
public class ChatController {

    private final ChatParticipantRepository chatParticipantRepository;

    @GetMapping("/list")
    public Response<SortedSet<ChatSummary>> findParticipatingChats(
            @AuthMember final Member member) {
        ChatParticipantDoc document = chatParticipantRepository.findById(member.getId())
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        SortedSet<ChatSummary> responses = document.getChatSummaries();
        return Response.onSuccess(responses);
    }
}
