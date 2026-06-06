package com.backend.allreva.support;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;

@ExtendWith(SpringExtension.class)
@ImportAutoConfiguration({RedisAutoConfiguration.class, JacksonAutoConfiguration.class})
public abstract class GlobalCacheTestSupport {

    @SuppressWarnings("resource")
    protected static final GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);

    static {
        redis.start();
    }

    @Autowired
    protected RedisConnectionFactory redisConnectionFactory;

    @BeforeEach
    void flushRedis() {
        redisConnectionFactory.getConnection().serverCommands().flushAll();
    }

    @DynamicPropertySource
    static void configureProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @TestConfiguration
    public static class StringRedisTemplateConfig {

        @Bean
        @Primary
        public RedisTemplate<String, String> stringValueRedisTemplate(final RedisConnectionFactory connectionFactory) {
            RedisTemplate<String, String> template = new RedisTemplate<>();
            template.setConnectionFactory(connectionFactory);
            template.setKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new StringRedisSerializer());
            template.setHashKeySerializer(new StringRedisSerializer());
            template.setHashValueSerializer(new StringRedisSerializer());
            return template;
        }
    }
}
