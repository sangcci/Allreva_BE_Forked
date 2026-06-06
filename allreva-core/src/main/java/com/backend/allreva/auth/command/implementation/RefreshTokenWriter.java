package com.backend.allreva.auth.command.implementation;

import com.backend.allreva.auth.domain.RefreshToken;
import com.backend.allreva.auth.domain.RefreshTokenStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenWriter {

    private final RefreshTokenStorage refreshTokenStorage;
    private final RefreshTokenReader refreshTokenReader;

    public void replace(final String token, final Long memberId) {
        refreshTokenReader.findByMemberId(memberId).ifPresent(refreshTokenStorage::delete);
        refreshTokenStorage.save(new RefreshToken(token, memberId));
    }

    public void deleteByToken(final String token) {
        refreshTokenStorage.findByToken(token).ifPresent(refreshTokenStorage::delete);
    }
}
