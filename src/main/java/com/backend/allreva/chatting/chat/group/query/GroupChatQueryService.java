package com.backend.allreva.chatting.chat.group.query;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.backend.allreva.chatting.chat.group.command.domain.GroupChatRepository;
import com.backend.allreva.chatting.chat.group.query.response.GroupChatDetailResponse;
import com.backend.allreva.chatting.chat.group.query.response.GroupChatOverviewResponse;
import com.backend.allreva.chatting.exception.ChattingErrorCode;
import com.backend.allreva.common.exception.CustomException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GroupChatQueryService {

    private final GroupChatRepository groupChatRepository;

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
