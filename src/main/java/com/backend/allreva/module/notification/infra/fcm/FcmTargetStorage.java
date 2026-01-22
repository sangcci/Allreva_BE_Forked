package com.backend.allreva.module.notification.infra.fcm;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.backend.allreva.module.notification.application.port.NotificationTargetStorage;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FcmTargetStorage implements NotificationTargetStorage {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public List<String> findTargetsByMemberIds(List<Long> memberIds) {
        List<String> keys = memberIds.stream()
                .map(this::buildRedisKey)
                .toList();
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
