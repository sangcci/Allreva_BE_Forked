package com.backend.allreva.module.chat.fixture;

import com.backend.allreva.module.chat.domain.message.Content;
import com.backend.allreva.module.chat.domain.message.ContentType;
import com.backend.allreva.module.chat.domain.message.GroupMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.test.util.ReflectionTestUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageFixture {

    public static GroupMessage createGroupMessage(
            Long chatId,
            Long number,
            Long senderId,
            String senderNickname) {
        GroupMessage message = new GroupMessage(
                chatId,
                number,
                new Content(ContentType.TEXT, "테스트 메시지"),
                senderId,
                senderNickname,
                "https://example.com/profile.jpg"
        );
        return message;
    }

    public static GroupMessage createGroupMessage(
            Long chatId,
            Long number,
            Long senderId,
            String senderNickname,
            String content) {
        GroupMessage message = new GroupMessage(
                chatId,
                number,
                new Content(ContentType.TEXT, content),
                senderId,
                senderNickname,
                "https://example.com/profile.jpg"
        );
        return message;
    }

    public static Content createContent(String text) {
        return new Content(ContentType.TEXT, text);
    }

    public static Content createImageContent(String imageUrl) {
        return new Content(ContentType.IMAGE, imageUrl);
    }
}
