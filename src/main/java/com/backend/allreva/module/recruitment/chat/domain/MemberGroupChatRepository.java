package com.backend.allreva.module.recruitment.chat.domain;

import java.util.Set;

public interface MemberGroupChatRepository {

    MemberGroupChat save(MemberGroupChat memberGroupChat);

    void deleteAllByGroupChatIdAndMemberId(Long groupChatId, Long memberId);

    Set<Long> findAllMemberIdByGroupChatId(Long groupChatId);
}
