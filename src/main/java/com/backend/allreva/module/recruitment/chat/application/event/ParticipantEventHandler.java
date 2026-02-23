package com.backend.allreva.module.recruitment.chat.application.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.backend.allreva.module.recruitment.chat.domain.GroupChat;
import com.backend.allreva.module.recruitment.chat.domain.GroupChatRepository;
import com.backend.allreva.module.recruitment.chat.domain.MemberGroupChatRepository;
import com.backend.allreva.module.recruitment.chat.domain.event.ChatCreatedEvent;
import com.backend.allreva.module.recruitment.chat.domain.event.ChatDeletedEvent;
import com.backend.allreva.module.recruitment.chat.domain.event.ChatMemberJoinedEvent;
import com.backend.allreva.module.recruitment.chat.domain.event.ChatMemberLeftEvent;
import com.backend.allreva.module.recruitment.chat.domain.event.ChatUpdatedEvent;
import com.backend.allreva.module.recruitment.chat.domain.participant.ChatParticipant;
import com.backend.allreva.module.recruitment.chat.domain.participant.ChatParticipantRepository;
import com.backend.allreva.module.recruitment.chat.domain.event.ChatEnteredEvent;
import com.backend.allreva.module.recruitment.chat.domain.participant.value.ChatInfoSummary;
import com.backend.allreva.module.recruitment.chat.domain.participant.value.ChatSummary;
import com.backend.allreva.module.recruitment.chat.domain.participant.value.ChatType;
import com.backend.allreva.module.recruitment.chat.domain.participant.value.PreviewMessage;
import com.backend.allreva.module.recruitment.chat.exception.ChattingErrorCode;
import com.backend.allreva.module.recruitment.chat.domain.message.GroupMessageRepository;
import com.backend.allreva.module.notification.infra.sse.event.SseConnectedEvent;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.member.domain.MemberRegisteredEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ParticipantEventHandler {

    private final GroupChatRepository groupChatRepository;

    private final GroupMessageRepository groupMessageRepository;

    private final MemberGroupChatRepository memberGroupChatRepository;

    private final ChatParticipantRepository participantRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMessage(final MemberRegisteredEvent event) {
        ChatParticipant participantDoc = new ChatParticipant(event.getMemberId());
        participantRepository.save(participantDoc);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMessage(final ChatEnteredEvent event) {
        ChatParticipant participantDoc = participantRepository.findById(event.getMemberId())
                .orElseThrow(() -> new CustomException(ChattingErrorCode.PARTICIPANT_NOT_FOUND));
        participantDoc.updateLastReadMessageNumber(
                event.getChatId(),
                event.getChatType(),
                event.getLastReadMessageNumber());
        participantRepository.save(participantDoc);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMessage(final SseConnectedEvent event) {
        ChatParticipant participant = participantRepository.findById(event.getMemberId())
                .orElseThrow(() -> new CustomException(ChattingErrorCode.PARTICIPANT_NOT_FOUND));

        SortedSet<ChatSummary> chatSummaries = participant.getChatSummaries();
        List<ChatSummary> summariesCopy = new ArrayList<>(chatSummaries);

        summariesCopy.forEach(summary -> {
            Long chatId = summary.getChatId();
            PreviewMessage previewMessage = findPreviewMessage(
                    summary.getChatType(),
                    chatId);
            participant.updatePreviewMessage(
                    chatId,
                    summary.getChatType(),
                    previewMessage);
        });
        participantRepository.save(participant);
    }

    private PreviewMessage findPreviewMessage(
            final ChatType chatType,
            final Long chatId) {
        return groupMessageRepository
                .findPreviewMessageByGroupChatId(chatId);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMessage(final ChatCreatedEvent event) {
        addGroupChatSummary(event.getMemberId(), event.getGroupChatId());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMessage(final ChatMemberJoinedEvent event) {
        addGroupChatSummary(event.getMemberId(), event.getGroupChatId());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMessage(final ChatUpdatedEvent event) {

        Set<Long> memberIds = memberGroupChatRepository
                .findAllMemberIdByGroupChatId(event.getGroupChatId());

        Set<ChatParticipant> participantDocs = participantRepository
                .findByMemberIdIn(memberIds);

        participantDocs.forEach(document -> document.updateChatInfoSummary(
                event.getGroupChatId(),
                ChatType.GROUP,
                event.getTitle(),
                event.getThumbnail()));
        participantRepository.saveAll(participantDocs);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMessage(final ChatMemberLeftEvent event) {
        removeGroupChat(event.getMemberId(), event.getGroupChatId());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMessage(final ChatDeletedEvent event) {
        removeGroupChat(event.getMemberId(), event.getGroupChatId());
    }

    // 채팅방 목록에 추가
    private void addGroupChatSummary(
            final Long memberId,
            final Long groupChatId) {
        ChatParticipant participantDoc = participantRepository.findChatParticipantByMemberId(memberId)
                .orElseGet(() -> {
                    ChatParticipant doc = new ChatParticipant(memberId);
                    participantRepository.save(doc);
                    return doc;
                });

        GroupChat groupChat = groupChatRepository.findById(groupChatId)
                .orElseThrow(() -> new CustomException(ChattingErrorCode.GROUP_CHAT_NOT_FOUND));

        ChatInfoSummary chatInfoSummary = new ChatInfoSummary(
                groupChat.getTitle().getValue(),
                groupChat.getThumbnail(),
                groupChat.getHeadcount());

        participantDoc.addChatSummary(
                groupChatId,
                ChatType.GROUP,
                chatInfoSummary);
        participantRepository.save(participantDoc);
    }

    private void removeGroupChat(
            final Long memberId,
            final Long groupChatId) {
        ChatParticipant participantDoc = participantRepository.findChatParticipantByMemberId(memberId)
                .orElseThrow(() -> new CustomException(ChattingErrorCode.PARTICIPANT_NOT_FOUND));

        participantDoc.removeChatRoom(groupChatId, ChatType.GROUP);
        participantRepository.save(participantDoc);
    }
}
