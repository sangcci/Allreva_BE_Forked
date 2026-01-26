package com.backend.allreva.module.chat.presentation;

import java.util.SortedSet;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.allreva.module.chat.domain.participant.ChatParticipant;
import com.backend.allreva.module.chat.domain.participant.ChatParticipantRepository;
import com.backend.allreva.module.chat.domain.participant.value.ChatSummary;
import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.member.exception.MemberErrorCode;
import com.backend.allreva.module.auth.security.AuthMember;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
@RestController
public class ChatParticipantController {

    private final ChatParticipantRepository chatParticipantRepository;

    @GetMapping("/list")
    public Response<SortedSet<ChatSummary>> findParticipatingChats(
            @AuthMember final Member member) {
        ChatParticipant document = chatParticipantRepository.findById(member.getId())
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        SortedSet<ChatSummary> responses = document.getChatSummaries();
        return Response.onSuccess(responses);
    }
}
