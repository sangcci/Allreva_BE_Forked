package com.backend.allreva.module.recruitment.chat.domain.participant;

import com.backend.allreva.module.recruitment.chat.infra.mongodb.ChatParticipantCustomRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.Set;

public interface ChatParticipantRepository extends MongoRepository<ChatParticipant, Long>, ChatParticipantCustomRepository {

    Optional<ChatParticipant> findChatParticipantByMemberId(Long memberId);

    Set<ChatParticipant> findByMemberIdIn(Set<Long> memberIds);
}
