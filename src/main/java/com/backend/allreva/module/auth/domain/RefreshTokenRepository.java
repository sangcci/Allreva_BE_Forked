package com.backend.allreva.module.auth.domain;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findRefreshTokenByMemberId(Long memberId);

    Optional<RefreshToken> findRefreshTokenByToken(String token);
}
