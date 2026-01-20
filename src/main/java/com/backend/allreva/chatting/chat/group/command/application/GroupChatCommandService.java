package com.backend.allreva.chatting.chat.group.command.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.allreva.chatting.chat.group.command.application.request.AddGroupChatRequest;
import com.backend.allreva.chatting.chat.group.command.application.request.UpdateGroupChatRequest;
import com.backend.allreva.chatting.chat.group.command.domain.GroupChat;
import com.backend.allreva.chatting.chat.group.command.domain.GroupChatRepository;
import com.backend.allreva.chatting.chat.group.command.domain.event.AddedGroupChatEvent;
import com.backend.allreva.chatting.exception.ChattingErrorCode;
import com.backend.allreva.common.event.Events;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.model.Image;
import com.backend.allreva.common.storage.upload.StorageUploadService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupChatCommandService {

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
        AddedGroupChatEvent addedEvent = new AddedGroupChatEvent(
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

}
