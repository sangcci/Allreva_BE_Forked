package com.backend.allreva.module.member.domain;

import com.backend.allreva.common.model.Email;
import com.backend.allreva.module.member.domain.value.LoginProvider;
import com.backend.allreva.module.member.domain.value.MemberRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findMemberByMemberRole(final MemberRole memberRole);

    Optional<Member> findByEmailAndLoginProvider(final Email email, final LoginProvider loginProvider);

    boolean existsByMemberInfoNickname(final String nickname);
}
