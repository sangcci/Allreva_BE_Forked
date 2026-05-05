package com.backend.allreva.module.search.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import com.backend.allreva.common.event.Events;
import com.backend.allreva.common.event.KeywordSearchedEvent;
import com.backend.allreva.support.IntegrationTestSupport;
import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("검색 키워드 이벤트 통합 테스트")
class KeywordSearchedEventIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private ZSetOperations<String, Object> zSetOperations;
    private static final String KEYWORD_KEY = "keyword";

    @BeforeEach
    void beforeEach() {
        zSetOperations = redisTemplate.opsForZSet();
    }

    @AfterEach
    void afterEach() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Nested
    @DisplayName("KeywordSearchedEvent 발행")
    class Describe_KeywordSearchedEvent_발행 {

        @Nested
        @DisplayName("이벤트가 발행되면")
        class Context_이벤트_발행 {

            @Test
            @DisplayName("Redis ZSet에 키워드 카운트가 1 증가한다")
            void Redis_ZSet에_키워드_카운트가_1_증가한다() {
                // given
                String keyword = "하현상";

                // when
                Events.raise(new KeywordSearchedEvent(keyword));

                // then
                await().atMost(Duration.ofSeconds(3))
                        .untilAsserted(() -> assertThat(zSetOperations.score(KEYWORD_KEY, keyword))
                                .isEqualTo(1.0));
            }
        }

        @Nested
        @DisplayName("같은 키워드 이벤트가 여러 번 발행되면")
        class Context_동일_키워드_이벤트_중복_발행 {

            @Test
            @DisplayName("키워드 카운트가 누적된다")
            void 키워드_카운트가_누적된다() {
                // given
                String keyword = "하현상";

                // when
                Events.raise(new KeywordSearchedEvent(keyword));
                Events.raise(new KeywordSearchedEvent(keyword));
                Events.raise(new KeywordSearchedEvent(keyword));

                // then
                await().atMost(Duration.ofSeconds(3))
                        .untilAsserted(() -> assertThat(zSetOperations.score(KEYWORD_KEY, keyword))
                                .isEqualTo(3.0));
            }
        }

        @Nested
        @DisplayName("서로 다른 키워드 이벤트가 발행되면")
        class Context_다른_키워드_이벤트_발행 {

            @Test
            @DisplayName("각 키워드 카운트가 독립적으로 증가한다")
            void 각_키워드_카운트가_독립적으로_증가한다() {
                // when
                Events.raise(new KeywordSearchedEvent("하현상"));
                Events.raise(new KeywordSearchedEvent("데이식스"));

                // then
                await().atMost(Duration.ofSeconds(3)).untilAsserted(() -> {
                    assertThat(zSetOperations.score(KEYWORD_KEY, "하현상")).isEqualTo(1.0);
                    assertThat(zSetOperations.score(KEYWORD_KEY, "데이식스")).isEqualTo(1.0);
                });
            }
        }
    }
}
