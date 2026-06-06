package com.backend.allreva.search.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.backend.allreva.common.config.RedisConfig;
import com.backend.allreva.search.domain.PopularKeywordChangeStatus;
import com.backend.allreva.search.domain.PopularKeywordRankItem;
import com.backend.allreva.search.domain.PopularKeywordRanks;
import com.backend.allreva.support.GlobalCacheTestSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;

@SuppressWarnings("NonAsciiCharacters")
@ContextConfiguration(classes = {RedisConfig.class, PopularKeywordRepositoryImpl.class})
@DisplayName("PopularKeywordRepositoryImpl 테스트")
class PopularKeywordRepositoryImplTest extends GlobalCacheTestSupport {

    @Autowired
    private PopularKeywordRepositoryImpl popularKeywordRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void 검색어_count를_증가시킨다() {
        popularKeywordRepository.recordSearch("아이유", 1.0);
        popularKeywordRepository.recordSearch("아이유", 2.0);

        Double score = redisTemplate.opsForZSet().score("keyword", "아이유");

        assertThat(score).isEqualTo(3.0);
    }

    @Test
    void score_역순으로_최대_10개_검색어를_조회한다() {
        for (int i = 1; i <= 11; i++) {
            popularKeywordRepository.recordSearch("keyword-" + i, (double) i);
        }

        List<String> result = popularKeywordRepository.getTop10Keywords();

        assertThat(result)
                .hasSize(10)
                .containsExactly(
                        "keyword-11",
                        "keyword-10",
                        "keyword-9",
                        "keyword-8",
                        "keyword-7",
                        "keyword-6",
                        "keyword-5",
                        "keyword-4",
                        "keyword-3",
                        "keyword-2");
    }

    @Test
    void 인기_검색어_rank_snapshot을_저장하고_조회한다() {
        PopularKeywordRanks ranks = new PopularKeywordRanks(List.of(PopularKeywordRankItem.builder()
                .rank(1)
                .keyword("아이유")
                .changeStatus(PopularKeywordChangeStatus.STAY)
                .build()));

        popularKeywordRepository.updateRank(ranks);

        assertThat(popularKeywordRepository.getPopularKeywordRank()).contains(ranks);
    }

    @Test
    void 모든_검색어_count를_절반으로_줄인다() {
        popularKeywordRepository.recordSearch("아이유", 10.0);
        popularKeywordRepository.recordSearch("데이식스", 4.0);

        popularKeywordRepository.decreaseAllKeywordCount();

        assertSoftly(softly -> {
            softly.assertThat(redisTemplate.opsForZSet().score("keyword", "아이유"))
                    .isEqualTo(5.0);
            softly.assertThat(redisTemplate.opsForZSet().score("keyword", "데이식스"))
                    .isEqualTo(2.0);
        });
    }

    @Test
    void 검색어_key가_없어도_count_감소는_실패하지_않는다() {
        popularKeywordRepository.decreaseAllKeywordCount();

        assertThat(popularKeywordRepository.getTop10Keywords()).isEmpty();
    }
}
