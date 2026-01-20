package com.backend.allreva.chatting.message.ui;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.backend.allreva.chatting.chat.integration.application.ChatParticipantService;
import com.backend.allreva.chatting.chat.integration.model.value.ChatType;
import com.backend.allreva.chatting.chat.integration.model.value.PreviewMessage;
import com.backend.allreva.chatting.message.command.MessageCommandService;
import com.backend.allreva.chatting.message.domain.GroupMessage;
import com.backend.allreva.chatting.message.domain.SingleMessage;
import com.backend.allreva.chatting.message.domain.value.Content;
import com.backend.allreva.chatting.notification.MessageSseService;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.member.command.domain.Member;
import com.backend.allreva.member.command.domain.MemberRepository;
import com.backend.allreva.member.exception.MemberErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class MessageCommandController {

    public static final String SUB_PERSONAL_DESTINATION = "/personal/room/";
    public static final String SUB_GROUP_DESTINATION = "/group/room/";

    private final MessageCommandService messageCommandService;
    private final MessageSseService messageSseService;

    private final ChatParticipantService chatParticipantService;
    private final MemberRepository memberRepository;

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/single/connection/{singleChatId}")
    public void sendSingleMessage(
            @DestinationVariable final Long singleChatId,
            @Payload final Content content,
            final SimpMessageHeaderAccessor accessor) {
        Member member = getMemberFromAccessor(accessor);

        String destination = SUB_PERSONAL_DESTINATION + singleChatId;
        SingleMessage singleMessage = messageCommandService
                .saveSingleMessage(singleChatId, content, member);

        messagingTemplate.convertAndSend(destination, singleMessage);

        PreviewMessage previewMessage = new PreviewMessage(
                singleMessage.getMessageNumber(),
                content.getPayload(),
                singleMessage.getSentAt());
        messageSseService.sendSummaryNotification(
                singleChatId,
                ChatType.SINGLE,
                previewMessage,
                member);
    }

    @MessageMapping("/group/connection/{groupChatId}")
    public void sendGroupMessage(
            @DestinationVariable final Long groupChatId,
            @Payload final Content content,
            final SimpMessageHeaderAccessor accessor) {
        Member member = getMemberFromAccessor(accessor);

        String destination = SUB_GROUP_DESTINATION + groupChatId;
        GroupMessage groupMessage = messageCommandService
                .saveGroupMessage(groupChatId, content, member);

        messagingTemplate.convertAndSend(destination, groupMessage);

        PreviewMessage previewMessage = new PreviewMessage(
                groupMessage.getMessageNumber(),
                content.getPayload(),
                groupMessage.getSentAt());
        messageSseService.sendSummaryNotification(
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
