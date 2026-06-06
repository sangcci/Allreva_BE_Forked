package com.backend.allreva.auth.domain;

import java.util.Optional;

public interface RefreshTokenStorage {

    RefreshToken save(RefreshToken refreshToken);

    void delete(RefreshToken refreshToken);

    void deleteAll();

    Optional<RefreshToken> findByMemberId(Long memberId);

    Optional<RefreshToken> findByToken(String token);
}
