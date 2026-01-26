package com.backend.allreva.module.chat.infra.jpa;

import com.backend.allreva.module.chat.application.dto.GroupChatDetailResponse;
import com.backend.allreva.module.chat.application.dto.GroupChatOverviewResponse;

import java.util.Optional;
import java.util.UUID;

public interface MemberGroupChatDslRepository {

    Optional<GroupChatOverviewResponse> findGroupChatOverview(
            UUID uuid
    );

    Optional<GroupChatDetailResponse> findGroupChatDetail(
            Long memberId,
            Long groupChatId
    );
}
