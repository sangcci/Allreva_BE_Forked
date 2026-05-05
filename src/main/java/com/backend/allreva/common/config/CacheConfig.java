package com.backend.allreva.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.List;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(List.of(
                build("concertMain", 100, Duration.ofDays(1)),
                build("concertSearch", 500, Duration.ofHours(1)),
                build("concertRelated", 200, Duration.ofHours(1)),
                build("concertHall", 200, Duration.ofDays(30))));
        return manager;
    }

    private CaffeineCache build(String name, int maxSize, Duration ttl) {
        return new CaffeineCache(
                name,
                Caffeine.newBuilder()
                        .maximumSize(maxSize)
                        .expireAfterWrite(ttl)
                        .recordStats()
                        .build());
    }
}
