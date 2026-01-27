package com.backend.allreva.module.chat.fixture;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.module.chat.domain.participant.ChatParticipant;
import com.backend.allreva.module.chat.domain.participant.value.ChatInfoSummary;
import com.backend.allreva.module.chat.domain.participant.value.ChatType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.test.util.ReflectionTestUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatParticipantFixture {

    public static ChatParticipant createChatParticipant(Long memberId) {
        ChatParticipant participant = new ChatParticipant(memberId);
        ReflectionTestUtils.setField(participant, "id", memberId);
        return participant;
    }

    public static ChatParticipant createChatParticipantWithChat(
            Long memberId,
            Long chatId,
            String chatTitle) {
        ChatParticipant participant = new ChatParticipant(memberId);
        ReflectionTestUtils.setField(participant, "id", memberId);

        ChatInfoSummary chatInfo = new ChatInfoSummary(chatTitle, new Image("thumbnail.jpg"), 1);
        participant.addChatSummary(chatId, ChatType.GROUP, chatInfo);

        return participant;
    }
}
