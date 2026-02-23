package com.backend.allreva.module.recruitment.chat.infra.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.backend.allreva.module.recruitment.chat.domain.GroupChat;

public interface GroupChatJpaRepository extends JpaRepository<GroupChat, Long> {

    Optional<GroupChat> findByUuid(UUID uuid);

    @Query("SELECT g.uuid FROM GroupChat g WHERE g.managerId = :memberId AND g.id = :groupChatId")
    UUID findGroupChatUuid(Long memberId, Long groupChatId);
}
