package com.backend.allreva.module.chat.fixture;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.module.chat.domain.GroupChat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GroupChatFixture {

    public static GroupChat createGroupChat(Long id, Long managerId) {
        GroupChat groupChat = GroupChat.builder()
                .title("테스트 채팅방")
                .managerId(managerId)
                .capacity(10)
                .thumbnail(new Image("https://example.com/thumbnail.jpg"))
                .build();
        ReflectionTestUtils.setField(groupChat, "id", id);
        return groupChat;
    }

    public static GroupChat createGroupChatWithTitle(Long id, Long managerId, String title) {
        GroupChat groupChat = GroupChat.builder()
                .title(title)
                .managerId(managerId)
                .capacity(10)
                .thumbnail(new Image("https://example.com/thumbnail.jpg"))
                .build();
        ReflectionTestUtils.setField(groupChat, "id", id);
        return groupChat;
    }

    public static GroupChat createGroupChatWithCapacity(Long id, Long managerId, int capacity) {
        GroupChat groupChat = GroupChat.builder()
                .title("테스트 채팅방")
                .managerId(managerId)
                .capacity(capacity)
                .thumbnail(new Image("https://example.com/thumbnail.jpg"))
                .build();
        ReflectionTestUtils.setField(groupChat, "id", id);
        return groupChat;
    }

    public static GroupChat createGroupChatWithUuid(Long id, Long managerId, UUID uuid) {
        GroupChat groupChat = GroupChat.builder()
                .title("테스트 채팅방")
                .managerId(managerId)
                .capacity(10)
                .thumbnail(new Image("https://example.com/thumbnail.jpg"))
                .build();
        ReflectionTestUtils.setField(groupChat, "id", id);
        ReflectionTestUtils.setField(groupChat, "uuid", uuid);
        return groupChat;
    }
}
