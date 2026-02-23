package com.backend.allreva.module.recruitment.chat.application;

import com.backend.allreva.module.recruitment.chat.domain.participant.ChatParticipant;
import com.backend.allreva.module.recruitment.chat.domain.participant.ChatParticipantRepository;
import com.backend.allreva.module.recruitment.chat.domain.participant.value.ChatType;
import com.backend.allreva.module.recruitment.chat.domain.participant.value.PreviewMessage;
import com.backend.allreva.module.recruitment.chat.domain.message.GroupMessageRepository;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ChatParticipantService {

    private final ChatParticipantRepository chatParticipantRepository;
    private final GroupMessageRepository groupMessageRepository;

    @Transactional
    public void updatePreviewMessage(
            final Long memberId,
            final Long chatId,
            final ChatType chatType,
            final long previewMessageNumber,
            final String previewText,
            final LocalDateTime sentAt) {
        ChatParticipant document = chatParticipantRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (document.existsInSummaries(chatId, chatType)) {
            document.updatePreviewMessage(
                    chatId,
                    chatType,
                    previewMessageNumber,
                    previewText,
                    sentAt);
            chatParticipantRepository.save(document);
        }
    }

    @Transactional
    public void updatePreviewMessage(
            final Long memberId,
            final Long chatId,
            final ChatType chatType,
            final PreviewMessage previewMessage) {
        ChatParticipant document = chatParticipantRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        if (document.existsInSummaries(chatId, chatType)) {
            document.updatePreviewMessage(
                    chatId,
                    chatType,
                    previewMessage);
            chatParticipantRepository.save(document);
        }
    }
}
