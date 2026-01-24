package com.backend.allreva.module.search.integration;

import com.backend.allreva.module.search.application.PopularKeywordService;
import com.backend.allreva.module.search.application.dto.ChangeStatus;
import com.backend.allreva.module.search.application.dto.PopularKeywordResponse;
import com.backend.allreva.module.search.application.dto.PopularKeywordResponses;
import com.backend.allreva.support.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("인기 검색어 통합 테스트")
class PopularKeywordCommandTest extends IntegrationTestSupport {

    @Autowired
    private PopularKeywordService keywordService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String KEYWORD_KEY = "keyword";
    private static final String POPULAR_KEYWORD_KEY = "popular-keyword";

    private ZSetOperations<String, Object> zSetOperations;
    private ValueOperations<String, Object> valueOperations;

    @BeforeEach
    void beforeEach() {
        zSetOperations = redisTemplate.opsForZSet();
        valueOperations = redisTemplate.opsForValue();
    }

    @AfterEach
    void afterEach() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Nested
    @DisplayName("키워드 카운트 업데이트")
    class Describe_키워드_카운트_업데이트 {

        @Nested
        @DisplayName("새로운 키워드를 검색할 때")
        class Context_새로운_키워드_검색 {

            @Test
            @DisplayName("키워드 카운트가 1로 증가한다")
            void 키워드_카운트가_1로_증가한다() {
                // given
                String keyword = "하현상";

                // when
                keywordService.updateKeywordCount(keyword);

                // then
                Double score = zSetOperations.score(KEYWORD_KEY, keyword);
                assertThat(score).isEqualTo(1.0);
            }
        }

        @Nested
        @DisplayName("기존 키워드를 다시 검색할 때")
        class Context_기존_키워드_재검색 {

            @Test
            @DisplayName("키워드 카운트가 누적된다")
            void 키워드_카운트가_누적된다() {
                // given
                String keyword = "하현상";
                keywordService.updateKeywordCount(keyword);
                keywordService.updateKeywordCount(keyword);

                // when
                keywordService.updateKeywordCount(keyword);

                // then
                Double score = zSetOperations.score(KEYWORD_KEY, keyword);
                assertThat(score).isEqualTo(3.0);
            }
        }
    }

    @Nested
    @DisplayName("인기 검색어 랭킹 업데이트")
    class Describe_인기_검색어_랭킹_업데이트 {

        @Nested
        @DisplayName("검색 횟수가 변경되었을 때")
        class Context_검색_횟수_변경 {

            @Test
            @DisplayName("랭킹이 업데이트되고 변동 상태가 반영된다")
            void 랭킹이_업데이트되고_변동_상태가_반영된다() {
                // given - 기존 랭킹 설정
                List<PopularKeywordResponse> keywordRankList = List.of(
                        PopularKeywordResponse.builder()
                                .rank(1)
                                .keyword("하현상")
                                .changeStatus(ChangeStatus.UP)
                                .build(),
                        PopularKeywordResponse.builder()
                                .rank(2)
                                .keyword("데이식스")
                                .changeStatus(ChangeStatus.DOWN)
                                .build(),
                        PopularKeywordResponse.builder()
                                .rank(3)
                                .keyword("아이유")
                                .changeStatus(ChangeStatus.STAY)
                                .build()
                );
                valueOperations.set(POPULAR_KEYWORD_KEY, new PopularKeywordResponses(keywordRankList));

                // given - 검색 카운트 변경
                zSetOperations.incrementScore(KEYWORD_KEY, "하현상", 25.0);
                zSetOperations.incrementScore(KEYWORD_KEY, "데이식스", 15.0);
                zSetOperations.incrementScore(KEYWORD_KEY, "아이유", 30.0);
                zSetOperations.incrementScore(KEYWORD_KEY, "아이브", 40.0); // 새로운 1위

                // when
                keywordService.updatePopularKeywordRank();

                // then
                List<PopularKeywordResponse> updatedRank = keywordService.getPopularKeywordRank();
                assertSoftly(softly -> {
                    softly.assertThat(updatedRank).isNotNull();
                    softly.assertThat(updatedRank).isNotEmpty();

                    // 1위는 아이브 (새로 진입)
                    softly.assertThat(updatedRank.get(0).keyword()).isEqualTo("아이브");
                    softly.assertThat(updatedRank.get(0).rank()).isEqualTo(1);
                    softly.assertThat(updatedRank.get(0).changeStatus()).isEqualTo(ChangeStatus.UP);
                });
            }

            @Test
            @DisplayName("랭킹이 올바른 순서로 정렬된다")
            void 랭킹이_올바른_순서로_정렬된다() {
                // given
                zSetOperations.incrementScore(KEYWORD_KEY, "키워드1", 10.0);
                zSetOperations.incrementScore(KEYWORD_KEY, "키워드2", 30.0);
                zSetOperations.incrementScore(KEYWORD_KEY, "키워드3", 20.0);

                // when
                keywordService.updatePopularKeywordRank();

                // then
                List<PopularKeywordResponse> updatedRank = keywordService.getPopularKeywordRank();
                assertSoftly(softly -> {
                    softly.assertThat(updatedRank.get(0).keyword()).isEqualTo("키워드2"); // 30
                    softly.assertThat(updatedRank.get(1).keyword()).isEqualTo("키워드3"); // 20
                    softly.assertThat(updatedRank.get(2).keyword()).isEqualTo("키워드1"); // 10
                });
            }
        }
    }

    @Nested
    @DisplayName("인기 검색어 조회")
    class Describe_인기_검색어_조회 {

        @Nested
        @DisplayName("랭킹 데이터가 존재할 때")
        class Context_랭킹_데이터_존재 {

            @Test
            @DisplayName("Top 10 랭킹이 반환된다")
            void Top_10_랭킹이_반환된다() {
                // given - 15개 키워드 추가 (Top 10만 반환되어야 함)
                for (int i = 1; i <= 15; i++) {
                    zSetOperations.incrementScore(KEYWORD_KEY, "키워드" + i, i * 10.0);
                }
                keywordService.updatePopularKeywordRank();

                // when
                List<PopularKeywordResponse> topRank = keywordService.getPopularKeywordRank();

                // then
                assertSoftly(softly -> {
                    softly.assertThat(topRank).hasSize(10);
                    softly.assertThat(topRank.get(0).rank()).isEqualTo(1);
                    softly.assertThat(topRank.get(9).rank()).isEqualTo(10);
                });
            }
        }
    }
}

