package com.backend.allreva.module.recruitment.chat.application.dto;

import com.backend.allreva.module.recruitment.chat.domain.participant.value.Participant;
import com.backend.allreva.module.recruitment.chat.domain.message.GroupMessage;
import com.backend.allreva.module.recruitment.chat.domain.message.Content;

import java.time.LocalDateTime;

public record MessageResponse(

        long messageNumber,

        Content content,

        Participant sender,
        LocalDateTime sentAt
) {

    public static MessageResponse from(
            final GroupMessage groupMessage
    ) {
        return new MessageResponse(
                groupMessage.getMessageNumber(),
                groupMessage.getContent(),
                groupMessage.getSender(),
                groupMessage.getSentAt()
        );
    }

}
