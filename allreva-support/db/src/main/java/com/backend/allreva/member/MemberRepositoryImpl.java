package com.backend.allreva.member;

import com.backend.allreva.common.model.Email;
import com.backend.allreva.member.domain.LoginProvider;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.member.domain.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member save(final Member member) {
        return memberJpaRepository.save(MemberEntity.from(member)).toDomain();
    }

    @Override
    public Optional<Member> findById(final Long id) {
        return memberJpaRepository.findById(id).map(MemberEntity::toDomain);
    }

    @Override
    public Optional<Member> findByEmailAndLoginProvider(final Email email, final LoginProvider loginProvider) {
        return memberJpaRepository
                .findByEmailAndLoginProvider(email.getEmail(), loginProvider)
                .map(MemberEntity::toDomain);
    }
}
