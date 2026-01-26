package com.backend.allreva.module.chat.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.allreva.module.chat.application.dto.AddGroupChatRequest;
import com.backend.allreva.module.chat.application.dto.GroupChatDetailResponse;
import com.backend.allreva.module.chat.application.dto.GroupChatOverviewResponse;
import com.backend.allreva.module.chat.application.dto.UpdateGroupChatRequest;
import com.backend.allreva.module.chat.domain.GroupChat;
import com.backend.allreva.module.chat.domain.GroupChatRepository;
import com.backend.allreva.module.chat.domain.event.ChatCreatedEvent;
import com.backend.allreva.module.chat.exception.ChattingErrorCode;
import com.backend.allreva.common.event.Events;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.model.Image;
import com.backend.allreva.common.storage.upload.StorageUploadService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupChatService {

    private final GroupChatRepository groupChatRepository;
    private final StorageUploadService storageUploadService;

    @Transactional
    public Long add(
            final AddGroupChatRequest request,
            final Image uploadedImage,
            final Long memberId) {
        GroupChat groupChat = GroupChat.builder()
                .title(request.title())
                .managerId(memberId)
                .thumbnail(uploadedImage)
                .capacity(request.capacity())
                .build();

        groupChatRepository.save(groupChat);
        ChatCreatedEvent addedEvent = new ChatCreatedEvent(
                groupChat.getId(),
                memberId);
        Events.raise(addedEvent);
        return groupChat.getId();
    }

    @Transactional
    public void update(
            final UpdateGroupChatRequest request,
            final Long memberId) {
        GroupChat groupChat = groupChatRepository.findById(request.groupChatId())
                .orElseThrow(() -> new CustomException(ChattingErrorCode.CHAT_ROOM_NOT_FOUND));

        groupChat.validateManager(memberId);
        groupChat.updateInfo(
                memberId,
                request.title(),
                request.description(),
                request.image());
    }

    @Transactional
    public Long join(
            final String uuid,
            final Long memberId) {
        GroupChat groupChat = groupChatRepository.findByUuid(UUID.fromString(uuid))
                .orElseThrow(() -> new CustomException(ChattingErrorCode.CHAT_ROOM_NOT_FOUND));

        groupChat.addHeadcount(memberId);

        return groupChat.getId();
    }

    @Transactional
    public void leave(
            final Long groupChatId,
            final Long memberId) {
        GroupChat groupChat = groupChatRepository.findById(groupChatId)
                .orElseThrow(() -> new CustomException(ChattingErrorCode.CHAT_ROOM_NOT_FOUND));
        groupChat.subtractHeadcount(memberId);
    }

    @Transactional
    public void delete(final Long groupChatId, final Long memberId) {
        GroupChat groupChat = groupChatRepository.findById(groupChatId)
                .orElseThrow(() -> new CustomException(ChattingErrorCode.CHAT_ROOM_NOT_FOUND));

        storageUploadService.deleteImage(groupChat.getThumbnail().getUrl());

        groupChat.validateForDelete(memberId);
        groupChatRepository.deleteById(groupChatId);
    }

    public GroupChatDetailResponse findGroupChatInfo(
            final Long memberId,
            final Long groupChatId) {
        return groupChatRepository.findGroupChatDetail(memberId, groupChatId)
                .orElseThrow(() -> new CustomException(ChattingErrorCode.GROUP_CHAT_NOT_FOUND));
    }

    public GroupChatOverviewResponse findOverview(
            final String uuid) {
        return groupChatRepository.findGroupChatOverview(UUID.fromString(uuid))
                .orElseThrow(() -> new CustomException(ChattingErrorCode.GROUP_CHAT_NOT_FOUND));
    }

    public String findInviteCode(
            final Long memberId,
            final Long groupChatId) {
        UUID uuid = groupChatRepository.findGroupChatUuid(memberId, groupChatId);
        return uuid.toString();
    }
}
