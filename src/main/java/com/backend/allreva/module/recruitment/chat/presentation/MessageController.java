package com.backend.allreva.module.recruitment.chat.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.auth.security.AuthMember;
import com.backend.allreva.module.recruitment.chat.application.MessageService;
import com.backend.allreva.module.recruitment.chat.application.dto.EnterChatResponse;
import com.backend.allreva.module.recruitment.chat.application.dto.MessageResponse;
import com.backend.allreva.module.member.domain.Member;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/message")
@RestController
class MessageController implements MessageControllerSwagger {

    private final MessageService messageService;

    @GetMapping("/group/enter")
    public Response<EnterChatResponse> enterGroupChat(
            @RequestParam("groupChatId") final Long groupChatId,
            @AuthMember final Member member) {
        EnterChatResponse response = messageService
                .findDefaultGroupMessages(groupChatId, member.getId());
        return Response.onSuccess(response);
    }

    @GetMapping("/group/read")
    public Response<List<MessageResponse>> findReadGroupMessages(
            @RequestParam("groupChatId") final Long groupChatId,
            @RequestParam("criteriaNumber") final long criteriaNumber) {
        List<MessageResponse> responses = messageService
                .findReadGroupMessages(groupChatId, criteriaNumber);
        return Response.onSuccess(responses);
    }

    @GetMapping("/group/unread")
    public Response<List<MessageResponse>> findUnreadGroupMessages(
            @RequestParam("groupChatId") final Long groupChatId,
            @RequestParam("criteriaNumber") final long criteriaNumber) {
        List<MessageResponse> responses = messageService
                .findUnreadGroupMessages(groupChatId, criteriaNumber);
        return Response.onSuccess(responses);
    }
}
