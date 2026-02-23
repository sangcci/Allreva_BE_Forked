package com.backend.allreva.module.recruitment.chat.infra.mongodb;

import com.backend.allreva.module.recruitment.chat.domain.participant.value.ChatType;

public interface ChatParticipantCustomRepository {
    Long findLastReadMessageNumber(
            Long memberId,
            Long chatId,
            ChatType chatType
    );
}
