package com.backend.allreva.auth;

import com.backend.allreva.auth.domain.RefreshToken;
import com.backend.allreva.auth.domain.RefreshTokenStorage;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenStorageAdapter implements RefreshTokenStorage {

    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(14);
    private static final String TOKEN_KEY_PREFIX = "refresh-token:token:";
    private static final String MEMBER_KEY_PREFIX = "refresh-token:member:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public RefreshToken save(final RefreshToken refreshToken) {
        redisTemplate.opsForValue().set(tokenKey(refreshToken.token()), refreshToken.memberId(), REFRESH_TOKEN_TTL);
        redisTemplate.opsForValue().set(memberKey(refreshToken.memberId()), refreshToken.token(), REFRESH_TOKEN_TTL);
        return refreshToken;
    }

    @Override
    public void delete(final RefreshToken refreshToken) {
        redisTemplate.delete(tokenKey(refreshToken.token()));
        redisTemplate.delete(memberKey(refreshToken.memberId()));
    }

    @Override
    public void deleteAll() {
        redisTemplate.delete(redisTemplate.keys(TOKEN_KEY_PREFIX + "*"));
        redisTemplate.delete(redisTemplate.keys(MEMBER_KEY_PREFIX + "*"));
    }

    @Override
    public Optional<RefreshToken> findByMemberId(final Long memberId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(memberKey(memberId)))
                .map(String::valueOf)
                .map(token -> new RefreshToken(token, memberId));
    }

    @Override
    public Optional<RefreshToken> findByToken(final String token) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(tokenKey(token)))
                .map(String::valueOf)
                .map(Long::valueOf)
                .map(memberId -> new RefreshToken(token, memberId));
    }

    private String tokenKey(final String token) {
        return TOKEN_KEY_PREFIX + token;
    }

    private String memberKey(final Long memberId) {
        return MEMBER_KEY_PREFIX + memberId;
    }
}
