package com.backend.allreva.notification.fcm;

import com.backend.allreva.notification.command.implementation.NotificationTargetReader;
import com.backend.allreva.notification.command.implementation.NotificationTargetWriter;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FcmTargetStorage implements NotificationTargetReader, NotificationTargetWriter {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Optional<String> get(final Long memberId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(buildRedisKey(memberId)));
    }

    @Override
    public List<String> getAll(final List<Long> memberIds) {
        List<String> keys = memberIds.stream().map(this::buildRedisKey).toList();
        return redisTemplate.opsForValue().multiGet(keys);
    }

    @Override
    public void save(Long memberId, String target) {
        String redisKey = buildRedisKey(memberId);
        redisTemplate.opsForValue().set(redisKey, target);
    }

    @Override
    public void delete(Long memberId) {
        String redisKey = buildRedisKey(memberId);
        redisTemplate.delete(redisKey);
    }

    /**
     * Redis 키 생성
     *
     * @param memberId 회원 ID
     * @return Redis 키 ("fcm:devicetoken:{memberId}")
     */
    private String buildRedisKey(final Long memberId) {
        return String.format("fcm:devicetoken:%d", memberId);
    }
}
