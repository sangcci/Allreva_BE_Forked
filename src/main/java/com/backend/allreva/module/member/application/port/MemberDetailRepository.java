package com.backend.allreva.module.member.application.port;

import com.backend.allreva.chatting.chat.single.command.domain.value.OtherMember;
import com.backend.allreva.module.member.application.dto.MemberDetailResponse;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberDetailRepository {
    MemberDetailResponse findById(final Long id);

    OtherMember findMemberSummary(Long memberId);
}
