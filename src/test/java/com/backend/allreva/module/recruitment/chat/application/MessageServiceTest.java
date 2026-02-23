package com.backend.allreva.module.recruitment.chat.application;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.module.recruitment.chat.application.dto.EnterChatResponse;
import com.backend.allreva.module.recruitment.chat.application.dto.MessageResponse;
import com.backend.allreva.module.recruitment.chat.domain.message.Content;
import com.backend.allreva.module.recruitment.chat.domain.message.GroupMessage;
import com.backend.allreva.module.recruitment.chat.domain.message.GroupMessageRepository;
import com.backend.allreva.module.recruitment.chat.domain.participant.ChatParticipantRepository;
import com.backend.allreva.module.recruitment.chat.domain.participant.value.ChatType;
import com.backend.allreva.module.recruitment.chat.domain.participant.value.Participant;
import com.backend.allreva.module.recruitment.chat.fixture.MessageFixture;
import com.backend.allreva.module.recruitment.chat.infra.mongodb.MessageCounterService;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.member.domain.value.MemberRole;
import com.backend.allreva.module.member.fixture.MemberFixture;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("MessageService 단위 테스트")
class MessageServiceTest {

    @InjectMocks
    private MessageService messageService;

    @Mock
    private GroupMessageRepository groupMessageRepository;

    @Mock
    private ChatParticipantRepository participantRepository;

    @Mock
    private MessageCounterService messageCounterService;

    @Nested
    @DisplayName("메시지 저장")
    class Describe_메시지_저장 {

        @Nested
        @DisplayName("유효한 메시지를 저장할 때")
        class Context_유효한_메시지_저장 {

            @Test
            @DisplayName("메시지가 성공적으로 저장되고 반환된다")
            void 메시지가_성공적으로_저장된다() {
                // given
                Long groupChatId = 100L;
                Long messageNumber = 1L;
                Member member = MemberFixture.createMember(1L, MemberRole.USER);
                Content content = MessageFixture.createContent("테스트 메시지");
                GroupMessage expectedMessage = MessageFixture.createGroupMessage(
                        groupChatId, messageNumber, member.getId(), member.getMemberInfo().getNickname());

                given(messageCounterService.getGroupMessageNumber(groupChatId)).willReturn(messageNumber);
                given(groupMessageRepository.save(any(GroupMessage.class))).willReturn(expectedMessage);

                // when
                GroupMessage result = messageService.saveMessage(groupChatId, content, member);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result).isNotNull();
                    softly.assertThat(result.getGroupChatId()).isEqualTo(groupChatId);
                });
                verify(messageCounterService, times(1)).getGroupMessageNumber(groupChatId);
                verify(groupMessageRepository, times(1)).save(any(GroupMessage.class));
            }
        }

        @Nested
        @DisplayName("연속으로 메시지를 저장할 때")
        class Context_연속_메시지_저장 {

            @Test
            @DisplayName("메시지 번호가 증가한다")
            void 메시지_번호가_증가한다() {
                // given
                Long groupChatId = 100L;
                Member member = MemberFixture.createMember(1L, MemberRole.USER);
                Content content1 = MessageFixture.createContent("첫 번째 메시지");
                Content content2 = MessageFixture.createContent("두 번째 메시지");

                GroupMessage message1 = MessageFixture.createGroupMessage(
                        groupChatId, 1L, member.getId(), member.getMemberInfo().getNickname());
                GroupMessage message2 = MessageFixture.createGroupMessage(
                        groupChatId, 2L, member.getId(), member.getMemberInfo().getNickname());

                given(messageCounterService.getGroupMessageNumber(groupChatId))
                        .willReturn(1L)
                        .willReturn(2L);
                given(groupMessageRepository.save(any(GroupMessage.class)))
                        .willReturn(message1)
                        .willReturn(message2);

                // when
                GroupMessage result1 = messageService.saveMessage(groupChatId, content1, member);
                GroupMessage result2 = messageService.saveMessage(groupChatId, content2, member);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result1.getMessageNumber()).isEqualTo(1L);
                    softly.assertThat(result2.getMessageNumber()).isEqualTo(2L);
                });
                verify(messageCounterService, times(2)).getGroupMessageNumber(groupChatId);
            }
        }
    }

    @Nested
    @DisplayName("채팅방 입장 시 기본 메시지 조회")
    class Describe_입장_시_기본_메시지_조회 {

        @Nested
        @DisplayName("채팅방에 입장할 때")
        class Context_채팅방_입장 {

            @Test
            @DisplayName("마지막으로 읽은 메시지 전후로 메시지가 조회된다")
            void 전후_메시지가_조회된다() {
                // given
                Long groupChatId = 100L;
                Long memberId = 1L;
                Long lastReadMessageNumber = 50L;
                Participant participant = new Participant(2L, "사용자2", new Image("profile.jpg"));
                List<MessageResponse> mockMessages = List.of(
                        new MessageResponse(45L, MessageFixture.createContent("이전 메시지"), participant, LocalDateTime.now()),
                        new MessageResponse(50L, MessageFixture.createContent("마지막 읽은 메시지"), participant, LocalDateTime.now()),
                        new MessageResponse(55L, MessageFixture.createContent("새 메시지"), participant, LocalDateTime.now())
                );

                given(participantRepository.findLastReadMessageNumber(memberId, groupChatId, ChatType.GROUP))
                        .willReturn(lastReadMessageNumber);
                given(groupMessageRepository.findMessageResponsesWithinRange(
                        groupChatId,
                        lastReadMessageNumber - MessageService.PAGING_UNIT,
                        lastReadMessageNumber + MessageService.PAGING_UNIT))
                        .willReturn(mockMessages);

                // when
                EnterChatResponse result = messageService.findDefaultGroupMessages(groupChatId, memberId);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result).isNotNull();
                    softly.assertThat(result.getMyId()).isEqualTo(memberId);
                    softly.assertThat(result.getLastReadMessageNumber()).isEqualTo(lastReadMessageNumber);
                    softly.assertThat(result.getMessages()).hasSize(3);
                });
                verify(participantRepository, times(1))
                        .findLastReadMessageNumber(memberId, groupChatId, ChatType.GROUP);
                verify(groupMessageRepository, times(1))
                        .findMessageResponsesWithinRange(anyLong(), anyLong(), anyLong());
            }
        }

        @Nested
        @DisplayName("처음 입장한 채팅방일 때")
        class Context_처음_입장한_채팅방 {

            @Test
            @DisplayName("최신 메시지들이 조회된다")
            void 최신_메시지들이_조회된다() {
                // given
                Long groupChatId = 100L;
                Long memberId = 1L;
                Long lastReadMessageNumber = 0L;
                List<MessageResponse> mockMessages = List.of();

                given(participantRepository.findLastReadMessageNumber(memberId, groupChatId, ChatType.GROUP))
                        .willReturn(lastReadMessageNumber);
                given(groupMessageRepository.findMessageResponsesWithinRange(anyLong(), anyLong(), anyLong()))
                        .willReturn(mockMessages);

                // when
                EnterChatResponse result = messageService.findDefaultGroupMessages(groupChatId, memberId);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result.getLastReadMessageNumber()).isEqualTo(0L);
                    softly.assertThat(result.getMessages()).isEmpty();
                });
            }
        }
    }

    @Nested
    @DisplayName("읽은 메시지 조회")
    class Describe_읽은_메시지_조회 {

        @Nested
        @DisplayName("이전 메시지를 조회할 때")
        class Context_이전_메시지_조회 {

            @Test
            @DisplayName("기준 메시지 번호 이전의 메시지들이 조회된다")
            void 이전_메시지들이_조회된다() {
                // given
                Long groupChatId = 100L;
                Long criteriaNumber = 50L;
                Participant participant = new Participant(2L, "사용자2", new Image("profile.jpg"));
                List<MessageResponse> mockMessages = List.of(
                        new MessageResponse(25L, MessageFixture.createContent("오래된 메시지1"), participant, LocalDateTime.now()),
                        new MessageResponse(30L, MessageFixture.createContent("오래된 메시지2"), participant, LocalDateTime.now()),
                        new MessageResponse(45L, MessageFixture.createContent("이전 메시지"), participant, LocalDateTime.now())
                );

                given(groupMessageRepository.findMessageResponsesWithinRange(
                        groupChatId,
                        criteriaNumber - MessageService.PAGING_UNIT,
                        criteriaNumber))
                        .willReturn(mockMessages);

                // when
                List<MessageResponse> result = messageService.findReadGroupMessages(groupChatId, criteriaNumber);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result).hasSize(3);
                    softly.assertThat(result).allMatch(msg -> msg.messageNumber() < criteriaNumber);
                });
                verify(groupMessageRepository, times(1))
                        .findMessageResponsesWithinRange(groupChatId, 25L, 50L);
            }
        }
    }

    @Nested
    @DisplayName("읽지 않은 메시지 조회")
    class Describe_읽지_않은_메시지_조회 {

        @Nested
        @DisplayName("새로운 메시지를 조회할 때")
        class Context_새로운_메시지_조회 {

            @Test
            @DisplayName("기준 메시지 번호 이후의 메시지들이 조회된다")
            void 이후_메시지들이_조회된다() {
                // given
                Long groupChatId = 100L;
                Long criteriaNumber = 50L;
                Participant participant = new Participant(2L, "사용자2", new Image("profile.jpg"));
                List<MessageResponse> mockMessages = List.of(
                        new MessageResponse(51L, MessageFixture.createContent("새 메시지1"), participant, LocalDateTime.now()),
                        new MessageResponse(55L, MessageFixture.createContent("새 메시지2"), participant, LocalDateTime.now()),
                        new MessageResponse(70L, MessageFixture.createContent("새 메시지3"), participant, LocalDateTime.now())
                );

                given(groupMessageRepository.findMessageResponsesWithinRange(
                        groupChatId,
                        criteriaNumber,
                        criteriaNumber + MessageService.PAGING_UNIT))
                        .willReturn(mockMessages);

                // when
                List<MessageResponse> result = messageService.findUnreadGroupMessages(groupChatId, criteriaNumber);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result).hasSize(3);
                    softly.assertThat(result).allMatch(msg -> msg.messageNumber() >= criteriaNumber);
                });
                verify(groupMessageRepository, times(1))
                        .findMessageResponsesWithinRange(groupChatId, 50L, 75L);
            }
        }

        @Nested
        @DisplayName("새로운 메시지가 없을 때")
        class Context_새로운_메시지_없음 {

            @Test
            @DisplayName("빈 목록이 반환된다")
            void 빈_목록이_반환된다() {
                // given
                Long groupChatId = 100L;
                Long criteriaNumber = 100L;
                List<MessageResponse> mockMessages = List.of();

                given(groupMessageRepository.findMessageResponsesWithinRange(anyLong(), anyLong(), anyLong()))
                        .willReturn(mockMessages);

                // when
                List<MessageResponse> result = messageService.findUnreadGroupMessages(groupChatId, criteriaNumber);

                // then
                assertThat(result).isEmpty();
            }
        }
    }
}
