package com.backend.allreva.module.chat.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.model.Image;
import com.backend.allreva.common.storage.upload.StorageUploadService;
import com.backend.allreva.module.chat.application.dto.AddGroupChatRequest;
import com.backend.allreva.module.chat.application.dto.GroupChatDetailResponse;
import com.backend.allreva.module.chat.application.dto.GroupChatOverviewResponse;
import com.backend.allreva.module.chat.application.dto.UpdateGroupChatRequest;
import com.backend.allreva.module.chat.domain.GroupChat;
import com.backend.allreva.module.chat.domain.GroupChatRepository;
import com.backend.allreva.module.chat.exception.ChattingErrorCode;
import com.backend.allreva.module.chat.fixture.ChatRequestFixture;
import com.backend.allreva.module.chat.fixture.GroupChatFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("GroupChatService 단위 테스트")
class GroupChatServiceTest {

    @InjectMocks
    private GroupChatService groupChatService;

    @Mock
    private GroupChatRepository groupChatRepository;

    @Mock
    private StorageUploadService storageUploadService;

    @Nested
    @DisplayName("그룹 채팅 생성")
    class Describe_그룹_채팅_생성 {

        @Nested
        @DisplayName("유효한 요청으로 그룹 채팅을 생성할 때")
        class Context_유효한_생성_요청 {

            @Test
            @DisplayName("그룹 채팅이 성공적으로 생성되고 ID가 반환된다")
            void 그룹_채팅이_성공적으로_생성된다() {
                // given
                Long memberId = 1L;
                Long expectedId = 100L;
                AddGroupChatRequest request = ChatRequestFixture.createAddGroupChatRequest();
                Image uploadedImage = new Image("https://example.com/image.jpg");

                // save 시 id를 설정하도록 Mock 동작 정의
                given(groupChatRepository.save(any(GroupChat.class))).willAnswer(invocation -> {
                    GroupChat saved = invocation.getArgument(0);
                    ReflectionTestUtils.setField(saved, "id", expectedId);
                    return saved;
                });

                // when
                Long result = groupChatService.add(request, uploadedImage, memberId);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result).isNotNull();
                    softly.assertThat(result).isEqualTo(expectedId);
                });
                verify(groupChatRepository, times(1)).save(any(GroupChat.class));
            }
        }
    }

    @Nested
    @DisplayName("그룹 채팅 수정")
    class Describe_그룹_채팅_수정 {

        @Nested
        @DisplayName("방장이 그룹 채팅 정보를 수정할 때")
        class Context_방장의_수정_요청 {

            @Test
            @DisplayName("그룹 채팅 정보가 성공적으로 수정된다")
            void 그룹_채팅_정보가_성공적으로_수정된다() {
                // given
                Long managerId = 1L;
                Long groupChatId = 100L;
                GroupChat groupChat = GroupChatFixture.createGroupChat(groupChatId, managerId);
                UpdateGroupChatRequest request = ChatRequestFixture.createUpdateGroupChatRequest(
                        groupChatId, "수정된 제목", "수정된 설명");

                given(groupChatRepository.findById(groupChatId)).willReturn(Optional.of(groupChat));

                // when
                groupChatService.update(request, managerId);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(groupChat.getTitle().getValue()).isEqualTo("수정된 제목");
                    softly.assertThat(groupChat.getDescription().getValue()).isEqualTo("수정된 설명");
                });
            }
        }

        @Nested
        @DisplayName("방장이 아닌 사용자가 그룹 채팅을 수정하려 할 때")
        class Context_방장이_아닌_사용자의_수정_요청 {

            @Test
            @DisplayName("예외가 발생한다")
            void 예외가_발생한다() {
                // given
                Long managerId = 1L;
                Long notManagerId = 2L;
                Long groupChatId = 100L;
                GroupChat groupChat = GroupChatFixture.createGroupChat(groupChatId, managerId);
                UpdateGroupChatRequest request = ChatRequestFixture.createUpdateGroupChatRequest(groupChatId);

                given(groupChatRepository.findById(groupChatId)).willReturn(Optional.of(groupChat));

                // when & then
                assertThatThrownBy(() -> groupChatService.update(request, notManagerId))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(ChattingErrorCode.INVALID_MANAGER.getMessage());
            }
        }

        @Nested
        @DisplayName("존재하지 않는 그룹 채팅을 수정하려 할 때")
        class Context_존재하지_않는_채팅방_수정 {

            @Test
            @DisplayName("예외가 발생한다")
            void 예외가_발생한다() {
                // given
                Long groupChatId = 999L;
                UpdateGroupChatRequest request = ChatRequestFixture.createUpdateGroupChatRequest(groupChatId);

                given(groupChatRepository.findById(groupChatId)).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> groupChatService.update(request, 1L))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(ChattingErrorCode.CHAT_ROOM_NOT_FOUND.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("그룹 채팅 참가")
    class Describe_그룹_채팅_참가 {

        @Nested
        @DisplayName("유효한 UUID로 그룹 채팅에 참가할 때")
        class Context_유효한_UUID로_참가 {

            @Test
            @DisplayName("참가가 성공하고 채팅방 ID가 반환된다")
            void 참가가_성공한다() {
                // given
                Long memberId = 1L;
                Long groupChatId = 100L;
                UUID uuid = UUID.randomUUID();
                GroupChat groupChat = GroupChatFixture.createGroupChatWithUuid(groupChatId, 2L, uuid);

                given(groupChatRepository.findByUuid(uuid)).willReturn(Optional.of(groupChat));

                // when
                Long result = groupChatService.join(uuid.toString(), memberId);

                // then
                assertThat(result).isEqualTo(groupChatId);
            }
        }

        @Nested
        @DisplayName("존재하지 않는 UUID로 참가하려 할 때")
        class Context_존재하지_않는_UUID로_참가 {

            @Test
            @DisplayName("예외가 발생한다")
            void 예외가_발생한다() {
                // given
                UUID uuid = UUID.randomUUID();

                given(groupChatRepository.findByUuid(uuid)).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> groupChatService.join(uuid.toString(), 1L))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(ChattingErrorCode.CHAT_ROOM_NOT_FOUND.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("그룹 채팅 나가기")
    class Describe_그룹_채팅_나가기 {

        @Nested
        @DisplayName("그룹 채팅에서 나갈 때")
        class Context_채팅방_나가기 {

            @Test
            @DisplayName("인원수가 감소한다")
            void 인원수가_감소한다() {
                // given
                Long memberId = 1L;
                Long groupChatId = 100L;
                GroupChat groupChat = GroupChatFixture.createGroupChat(groupChatId, 2L);
                groupChat.addHeadcount(memberId);
                int initialHeadcount = groupChat.getHeadcount();

                given(groupChatRepository.findById(groupChatId)).willReturn(Optional.of(groupChat));

                // when
                groupChatService.leave(groupChatId, memberId);

                // then
                assertThat(groupChat.getHeadcount()).isEqualTo(initialHeadcount - 1);
            }
        }

        @Nested
        @DisplayName("존재하지 않는 그룹 채팅에서 나가려 할 때")
        class Context_존재하지_않는_채팅방_나가기 {

            @Test
            @DisplayName("예외가 발생한다")
            void 예외가_발생한다() {
                // given
                Long groupChatId = 999L;

                given(groupChatRepository.findById(groupChatId)).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> groupChatService.leave(groupChatId, 1L))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(ChattingErrorCode.CHAT_ROOM_NOT_FOUND.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("그룹 채팅 삭제")
    class Describe_그룹_채팅_삭제 {

        @Nested
        @DisplayName("방장이 인원이 1명인 그룹 채팅을 삭제할 때")
        class Context_방장의_삭제_요청 {

            @Test
            @DisplayName("그룹 채팅이 성공적으로 삭제된다")
            void 그룹_채팅이_성공적으로_삭제된다() {
                // given
                Long managerId = 1L;
                Long groupChatId = 100L;
                GroupChat groupChat = GroupChatFixture.createGroupChat(groupChatId, managerId);
                // 초기 headcount는 1 (생성 시 자동 설정)

                given(groupChatRepository.findById(groupChatId)).willReturn(Optional.of(groupChat));

                // when
                groupChatService.delete(groupChatId, managerId);

                // then
                verify(storageUploadService, times(1)).deleteImage(anyString());
                verify(groupChatRepository, times(1)).deleteById(groupChatId);
            }
        }

        @Nested
        @DisplayName("방장이 아닌 사용자가 그룹 채팅을 삭제하려 할 때")
        class Context_방장이_아닌_사용자의_삭제_요청 {

            @Test
            @DisplayName("예외가 발생한다")
            void 예외가_발생한다() {
                // given
                Long managerId = 1L;
                Long notManagerId = 2L;
                Long groupChatId = 100L;
                GroupChat groupChat = GroupChatFixture.createGroupChat(groupChatId, managerId);
                // 초기 headcount는 1 (생성 시 자동 설정)

                given(groupChatRepository.findById(groupChatId)).willReturn(Optional.of(groupChat));

                // when & then
                assertThatThrownBy(() -> groupChatService.delete(groupChatId, notManagerId))
                        .isInstanceOf(CustomException.class);
            }
        }

        @Nested
        @DisplayName("인원이 2명 이상인 그룹 채팅을 삭제하려 할 때")
        class Context_인원이_여러명인_채팅방_삭제 {

            @Test
            @DisplayName("예외가 발생한다")
            void 예외가_발생한다() {
                // given
                Long managerId = 1L;
                Long groupChatId = 100L;
                GroupChat groupChat = GroupChatFixture.createGroupChat(groupChatId, managerId);
                // 초기 headcount는 1, 멤버 추가로 2명으로 만듦
                groupChat.addHeadcount(2L);

                given(groupChatRepository.findById(groupChatId)).willReturn(Optional.of(groupChat));

                // when & then
                assertThatThrownBy(() -> groupChatService.delete(groupChatId, managerId))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(ChattingErrorCode.DO_NOT_MEET_CONDITIONS_TO_DELETE.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("그룹 채팅 상세 조회")
    class Describe_그룹_채팅_상세_조회 {

        @Nested
        @DisplayName("참가 중인 그룹 채팅의 상세 정보를 조회할 때")
        class Context_참가_중인_채팅방_조회 {

            @Test
            @DisplayName("상세 정보가 성공적으로 조회된다")
            void 상세_정보가_조회된다() {
                // given
                Long memberId = 1L;
                Long groupChatId = 100L;
                GroupChatDetailResponse response = mock(GroupChatDetailResponse.class);

                given(groupChatRepository.findGroupChatDetail(memberId, groupChatId))
                        .willReturn(Optional.of(response));

                // when
                GroupChatDetailResponse result = groupChatService.findGroupChatInfo(memberId, groupChatId);

                // then
                assertThat(result).isNotNull();
                verify(groupChatRepository, times(1)).findGroupChatDetail(memberId, groupChatId);
            }
        }

        @Nested
        @DisplayName("참가하지 않은 그룹 채팅을 조회하려 할 때")
        class Context_참가하지_않은_채팅방_조회 {

            @Test
            @DisplayName("예외가 발생한다")
            void 예외가_발생한다() {
                // given
                Long memberId = 1L;
                Long groupChatId = 999L;

                given(groupChatRepository.findGroupChatDetail(memberId, groupChatId))
                        .willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> groupChatService.findGroupChatInfo(memberId, groupChatId))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(ChattingErrorCode.GROUP_CHAT_NOT_FOUND.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("그룹 채팅 개요 조회")
    class Describe_그룹_채팅_개요_조회 {

        @Nested
        @DisplayName("유효한 UUID로 그룹 채팅 개요를 조회할 때")
        class Context_유효한_UUID로_개요_조회 {

            @Test
            @DisplayName("개요 정보가 성공적으로 조회된다")
            void 개요_정보가_조회된다() {
                // given
                UUID uuid = UUID.randomUUID();
                GroupChatOverviewResponse response = mock(GroupChatOverviewResponse.class);

                given(groupChatRepository.findGroupChatOverview(uuid))
                        .willReturn(Optional.of(response));

                // when
                GroupChatOverviewResponse result = groupChatService.findOverview(uuid.toString());

                // then
                assertThat(result).isNotNull();
                verify(groupChatRepository, times(1)).findGroupChatOverview(uuid);
            }
        }

        @Nested
        @DisplayName("존재하지 않는 UUID로 조회하려 할 때")
        class Context_존재하지_않는_UUID로_조회 {

            @Test
            @DisplayName("예외가 발생한다")
            void 예외가_발생한다() {
                // given
                UUID uuid = UUID.randomUUID();

                given(groupChatRepository.findGroupChatOverview(uuid))
                        .willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> groupChatService.findOverview(uuid.toString()))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(ChattingErrorCode.GROUP_CHAT_NOT_FOUND.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("초대 코드 조회")
    class Describe_초대_코드_조회 {

        @Nested
        @DisplayName("그룹 채팅의 초대 코드를 조회할 때")
        class Context_초대_코드_조회 {

            @Test
            @DisplayName("UUID 문자열이 반환된다")
            void UUID_문자열이_반환된다() {
                // given
                Long memberId = 1L;
                Long groupChatId = 100L;
                UUID uuid = UUID.randomUUID();

                given(groupChatRepository.findGroupChatUuid(memberId, groupChatId))
                        .willReturn(uuid);

                // when
                String result = groupChatService.findInviteCode(memberId, groupChatId);

                // then
                assertThat(result).isEqualTo(uuid.toString());
                verify(groupChatRepository, times(1)).findGroupChatUuid(memberId, groupChatId);
            }
        }
    }
}
