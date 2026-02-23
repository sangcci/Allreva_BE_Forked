package com.backend.allreva.module.recruitment.chat.domain;

import java.util.Optional;
import java.util.UUID;

import com.backend.allreva.module.recruitment.chat.application.dto.GroupChatDetailResponse;
import com.backend.allreva.module.recruitment.chat.application.dto.GroupChatOverviewResponse;

public interface GroupChatRepository {

    GroupChat save(GroupChat groupChat);

    Optional<GroupChat> findById(Long id);

    Optional<GroupChat> findByUuid(UUID uuid);

    void deleteById(Long id);

    UUID findGroupChatUuid(Long memberId, Long groupChatId);

    Optional<GroupChatDetailResponse> findGroupChatDetail(Long memberId, Long groupChatId);

    Optional<GroupChatOverviewResponse> findGroupChatOverview(UUID uuid);
}
