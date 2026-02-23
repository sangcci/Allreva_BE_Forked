package com.backend.allreva.module.recruitment.chat.application;

import com.backend.allreva.module.recruitment.chat.domain.participant.ChatParticipantRepository;
import com.backend.allreva.module.recruitment.chat.domain.participant.value.ChatType;
import com.backend.allreva.module.recruitment.chat.application.dto.EnterChatResponse;
import com.backend.allreva.module.recruitment.chat.application.dto.MessageResponse;
import com.backend.allreva.module.recruitment.chat.domain.message.Content;
import com.backend.allreva.module.recruitment.chat.domain.message.GroupMessage;
import com.backend.allreva.module.recruitment.chat.domain.message.GroupMessageRepository;
import com.backend.allreva.module.recruitment.chat.infra.mongodb.MessageCounterService;
import com.backend.allreva.module.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MessageService {

    public static final int PAGING_UNIT = 25;

    private final GroupMessageRepository groupMessageRepository;
    private final ChatParticipantRepository participantRepository;
    private final MessageCounterService messageCounterService;

    @Transactional
    public GroupMessage saveMessage(
            final Long groupChatId,
            final Content content,
            final Member member
    ) {
        long number = messageCounterService.getGroupMessageNumber(groupChatId);
        GroupMessage groupMessage = new GroupMessage(
                groupChatId,
                number,
                content,
                member.getId(),
                member.getMemberInfo().getNickname(),
                member.getMemberInfo().getProfileImageUrl()
        );
        return groupMessageRepository.save(groupMessage);
    }

    public EnterChatResponse findDefaultGroupMessages(
            final Long groupChatId,
            final long memberId
    ) {
        Long lastReadMessageNumber = participantRepository
                .findLastReadMessageNumber(
                        memberId,
                        groupChatId,
                        ChatType.GROUP
                );
        List<MessageResponse> messageResponses = groupMessageRepository.findMessageResponsesWithinRange(
                groupChatId,
                lastReadMessageNumber - PAGING_UNIT,
                lastReadMessageNumber + PAGING_UNIT
        );
        return new EnterChatResponse(
                memberId,
                lastReadMessageNumber,
                messageResponses
        );
    }

    public List<MessageResponse> findReadGroupMessages(
            final Long groupChatId,
            final long criteriaNumber
    ) {
        return groupMessageRepository.findMessageResponsesWithinRange(
                groupChatId,
                criteriaNumber - PAGING_UNIT,
                criteriaNumber
        );
    }

    public List<MessageResponse> findUnreadGroupMessages(
            final Long groupChatId,
            final long criteriaNumber
    ) {
        return groupMessageRepository.findMessageResponsesWithinRange(
                groupChatId,
                criteriaNumber,
                criteriaNumber + PAGING_UNIT
        );
    }

}
