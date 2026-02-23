package com.backend.allreva.module.recruitment.chat.fixture;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.module.recruitment.chat.application.dto.AddGroupChatRequest;
import com.backend.allreva.module.recruitment.chat.application.dto.UpdateGroupChatRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChatRequestFixture {

    public static AddGroupChatRequest createAddGroupChatRequest() {
        return new AddGroupChatRequest("테스트 채팅방", 10);
    }

    public static AddGroupChatRequest createAddGroupChatRequest(String title, int capacity) {
        return new AddGroupChatRequest(title, capacity);
    }

    public static UpdateGroupChatRequest createUpdateGroupChatRequest(Long groupChatId) {
        return new UpdateGroupChatRequest(
                groupChatId,
                "수정된 채팅방",
                "수정된 설명",
                new Image("https://example.com/updated.jpg")
        );
    }

    public static UpdateGroupChatRequest createUpdateGroupChatRequest(
            Long groupChatId,
            String title,
            String description) {
        return new UpdateGroupChatRequest(
                groupChatId,
                title,
                description,
                new Image("https://example.com/image.jpg")
        );
    }
}
