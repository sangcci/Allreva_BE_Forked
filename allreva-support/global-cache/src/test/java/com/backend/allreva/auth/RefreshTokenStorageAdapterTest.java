package com.backend.allreva.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.backend.allreva.auth.domain.RefreshToken;
import com.backend.allreva.common.config.RedisConfig;
import com.backend.allreva.support.GlobalCacheTestSupport;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;

@SuppressWarnings("NonAsciiCharacters")
@ContextConfiguration(classes = {RedisConfig.class, RefreshTokenStorageAdapter.class})
@DisplayName("RefreshTokenStorageAdapter 테스트")
class RefreshTokenStorageAdapterTest extends GlobalCacheTestSupport {

    @Autowired
    private RefreshTokenStorageAdapter refreshTokenStorageAdapter;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void refresh_token을_양방향으로_저장하고_조회한다() {
        RefreshToken refreshToken = new RefreshToken("refresh-token", 1L);

        refreshTokenStorageAdapter.save(refreshToken);

        assertSoftly(softly -> {
            softly.assertThat(refreshTokenStorageAdapter.findByToken("refresh-token"))
                    .contains(refreshToken);
            softly.assertThat(refreshTokenStorageAdapter.findByMemberId(1L)).contains(refreshToken);
            softly.assertThat(redisTemplate.getExpire("refresh-token:token:refresh-token"))
                    .isPositive();
            softly.assertThat(redisTemplate.getExpire("refresh-token:member:1")).isPositive();
        });
    }

    @Test
    void refresh_token을_삭제하면_양방향_key가_제거된다() {
        RefreshToken refreshToken = new RefreshToken("refresh-token", 1L);
        refreshTokenStorageAdapter.save(refreshToken);

        refreshTokenStorageAdapter.delete(refreshToken);

        assertSoftly(softly -> {
            softly.assertThat(refreshTokenStorageAdapter.findByToken("refresh-token"))
                    .isEmpty();
            softly.assertThat(refreshTokenStorageAdapter.findByMemberId(1L)).isEmpty();
        });
    }

    @Test
    void 모든_refresh_token_key를_삭제한다() {
        refreshTokenStorageAdapter.save(new RefreshToken("refresh-token-1", 1L));
        refreshTokenStorageAdapter.save(new RefreshToken("refresh-token-2", 2L));

        refreshTokenStorageAdapter.deleteAll();

        assertSoftly(softly -> {
            softly.assertThat(refreshTokenStorageAdapter.findByToken("refresh-token-1"))
                    .isEmpty();
            softly.assertThat(refreshTokenStorageAdapter.findByMemberId(1L)).isEmpty();
            softly.assertThat(refreshTokenStorageAdapter.findByToken("refresh-token-2"))
                    .isEmpty();
            softly.assertThat(refreshTokenStorageAdapter.findByMemberId(2L)).isEmpty();
        });
    }

    @Test
    void 저장되지_않은_refresh_token은_empty로_조회된다() {
        Optional<RefreshToken> result = refreshTokenStorageAdapter.findByToken("unknown-token");

        assertThat(result).isEmpty();
    }
}
