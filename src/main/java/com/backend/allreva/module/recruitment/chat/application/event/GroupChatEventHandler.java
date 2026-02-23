package com.backend.allreva.module.recruitment.chat.application.event;

import com.backend.allreva.module.recruitment.chat.domain.MemberGroupChat;
import com.backend.allreva.module.recruitment.chat.domain.MemberGroupChatRepository;
import com.backend.allreva.module.recruitment.chat.domain.event.ChatCreatedEvent;
import com.backend.allreva.module.recruitment.chat.domain.event.ChatDeletedEvent;
import com.backend.allreva.module.recruitment.chat.domain.event.ChatMemberJoinedEvent;
import com.backend.allreva.module.recruitment.chat.domain.event.ChatMemberLeftEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Service
public class GroupChatEventHandler {

    private final MemberGroupChatRepository memberGroupChatRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMessage(final ChatCreatedEvent event) {
        MemberGroupChat memberGroupChat = new MemberGroupChat(event.getMemberId(), event.getGroupChatId());
        memberGroupChatRepository.save(memberGroupChat);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMessage(final ChatMemberJoinedEvent event) {
        MemberGroupChat memberGroupChat = new MemberGroupChat(event.getMemberId(), event.getGroupChatId());
        memberGroupChatRepository.save(memberGroupChat);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMessage(final ChatMemberLeftEvent event) {
        memberGroupChatRepository.deleteAllByGroupChatIdAndMemberId(event.getGroupChatId(), event.getMemberId());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onMessage(final ChatDeletedEvent event) {
        memberGroupChatRepository.deleteAllByGroupChatIdAndMemberId(event.getGroupChatId(), event.getMemberId());
    }

}
