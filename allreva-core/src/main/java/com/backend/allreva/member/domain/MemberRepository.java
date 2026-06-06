package com.backend.allreva.member.domain;

import com.backend.allreva.common.model.Email;
import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findById(Long id);

    Optional<Member> findByEmailAndLoginProvider(Email email, LoginProvider loginProvider);
}
