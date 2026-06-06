package com.backend.allreva.member;

import com.backend.allreva.member.domain.LoginProvider;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberJpaRepository extends JpaRepository<MemberEntity, Long> {

    Optional<MemberEntity> findByEmailAndLoginProvider(String email, LoginProvider loginProvider);

    boolean existsByNickname(String nickname);
}
