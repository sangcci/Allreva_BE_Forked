package com.backend.allreva.module.recruitment.chat.infra;

import java.util.Set;

import org.springframework.stereotype.Repository;

import com.backend.allreva.module.recruitment.chat.domain.MemberGroupChat;
import com.backend.allreva.module.recruitment.chat.domain.MemberGroupChatRepository;
import com.backend.allreva.module.recruitment.chat.infra.jpa.MemberGroupChatJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberGroupChatRepositoryImpl implements MemberGroupChatRepository {

    private final MemberGroupChatJpaRepository jpa;

    @Override
    public MemberGroupChat save(final MemberGroupChat memberGroupChat) {
        return jpa.save(memberGroupChat);
    }

    @Override
    public void deleteAllByGroupChatIdAndMemberId(final Long groupChatId, final Long memberId) {
        jpa.deleteAllByGroupChatIdAndMemberId(groupChatId, memberId);
    }

    @Override
    public Set<Long> findAllMemberIdByGroupChatId(final Long groupChatId) {
        return jpa.findAllMemberIdByGroupChatId(groupChatId);
    }
}
