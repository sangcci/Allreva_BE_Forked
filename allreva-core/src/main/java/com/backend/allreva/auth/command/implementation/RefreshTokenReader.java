package com.backend.allreva.auth.command.implementation;

import com.backend.allreva.auth.domain.JwtErrorCode;
import com.backend.allreva.auth.domain.RefreshToken;
import com.backend.allreva.auth.domain.RefreshTokenStorage;
import com.backend.allreva.common.exception.CustomException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenReader {

    private final RefreshTokenStorage refreshTokenStorage;

    public RefreshToken getByToken(final String token) {
        return refreshTokenStorage
                .findByToken(token)
                .orElseThrow(() -> new CustomException(JwtErrorCode.TOKEN_NOT_FOUND));
    }

    public Optional<RefreshToken> findByMemberId(final Long memberId) {
        return refreshTokenStorage.findByMemberId(memberId);
    }
}
