package com.backend.allreva.module.recruitment.chat.infra.jpa;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.backend.allreva.module.recruitment.chat.domain.MemberGroupChat;

public interface MemberGroupChatJpaRepository extends JpaRepository<MemberGroupChat, Long> {

    void deleteAllByGroupChatIdAndMemberId(Long groupChatId, Long memberId);

    @Query("SELECT m.memberId FROM MemberGroupChat m WHERE m.groupChatId = :groupChatId")
    Set<Long> findAllMemberIdByGroupChatId(Long groupChatId);
}
