package com.backend.allreva.module.recruitment.chat.infra.mongodb;

import com.backend.allreva.module.recruitment.chat.domain.participant.value.PreviewMessage;
import com.backend.allreva.module.recruitment.chat.application.dto.MessageResponse;

import java.util.List;

public interface GroupMessageCustomRepository {
    PreviewMessage findPreviewMessageByGroupChatId(
            Long groupChatId
    );

    List<MessageResponse> findMessageResponsesWithinRange(
            Long groupChatId,
            long fromNumber,
            long toNumber
    );
}
