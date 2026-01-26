package com.backend.allreva.module.chat.presentation;

import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.auth.security.AuthMember;
import com.backend.allreva.module.notification.infra.sse.ChatSseService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
@RestController
public class ChatSseController {

    private final ChatSseService chatSseService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connectNotification(@AuthMember final Member member) {
        return chatSseService.connect(member.getId());
    }

}
