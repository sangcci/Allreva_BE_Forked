package com.backend.allreva.member;

import com.backend.allreva.member.domain.NicknameDuplication;
import com.backend.allreva.member.query.implementation.MemberFinderPort;
import com.backend.allreva.member.query.model.MemberDetailResult;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberFinderAdapter implements MemberFinderPort {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Optional<MemberDetailResult> findById(final Long id) {
        return memberJpaRepository.findById(id).map(MemberEntity::toDomain).map(MemberDetailResult::from);
    }

    @Override
    public NicknameDuplication findNicknameDuplication(final String nickname) {
        return new NicknameDuplication(memberJpaRepository.existsByNickname(nickname));
    }
}
