package com.backend.allreva.module.recruitment.chat.presentation;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.recruitment.chat.application.MessageService;
import com.backend.allreva.module.recruitment.chat.domain.message.Content;
import com.backend.allreva.module.recruitment.chat.domain.message.GroupMessage;
import com.backend.allreva.module.recruitment.chat.domain.participant.value.ChatType;
import com.backend.allreva.module.recruitment.chat.domain.participant.value.PreviewMessage;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.member.domain.MemberRepository;
import com.backend.allreva.module.member.exception.MemberErrorCode;
import com.backend.allreva.module.notification.infra.sse.ChatSseService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
class MessageWebSocketController {

    public static final String SUB_GROUP_DESTINATION = "/group/room/";

    private final MessageService messageService;
    private final ChatSseService chatSseService;

    private final MemberRepository memberRepository;

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/group/connection/{groupChatId}")
    public void sendGroupMessage(
            @DestinationVariable final Long groupChatId,
            @Payload final Content content,
            final SimpMessageHeaderAccessor accessor) {
        Member member = getMemberFromAccessor(accessor);

        String destination = SUB_GROUP_DESTINATION + groupChatId;
        GroupMessage groupMessage = messageService
                .saveMessage(groupChatId, content, member);

        messagingTemplate.convertAndSend(destination, groupMessage);

        PreviewMessage previewMessage = new PreviewMessage(
                groupMessage.getMessageNumber(),
                content.getPayload(),
                groupMessage.getSentAt());
        chatSseService.sendChatNotification(
                groupChatId,
                ChatType.GROUP,
                previewMessage,
                member);
    }

    private Member getMemberFromAccessor(SimpMessageHeaderAccessor accessor) {
        String attribute = (String) accessor.getSessionAttributes().get("memberId");
        Long memberId = Long.parseLong(attribute);
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
