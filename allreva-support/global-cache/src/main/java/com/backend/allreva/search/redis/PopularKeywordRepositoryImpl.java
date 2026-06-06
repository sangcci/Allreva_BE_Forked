package com.backend.allreva.search.redis;

import com.backend.allreva.search.domain.PopularKeywordRanks;
import com.backend.allreva.search.domain.PopularKeywordRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

@Repository
public class PopularKeywordRepositoryImpl implements PopularKeywordRepository {
    private static final String KEYWORD_KEY = "keyword";
    private static final String POPULAR_KEYWORD_KEY = "popular-keyword";

    private final ZSetOperations<String, Object> zSetOperations;
    private final ValueOperations<String, Object> valueOperations;

    public PopularKeywordRepositoryImpl(final RedisTemplate<String, Object> redisTemplate) {
        this.zSetOperations = redisTemplate.opsForZSet();
        this.valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public Optional<PopularKeywordRanks> getPopularKeywordRank() {
        return Optional.ofNullable((PopularKeywordRanks) valueOperations.get(POPULAR_KEYWORD_KEY));
    }

    @Override
    public void updateRank(final PopularKeywordRanks list) {
        valueOperations.set(POPULAR_KEYWORD_KEY, list);
    }

    @Override
    public void recordSearch(final String keyword, final Double count) {
        zSetOperations.incrementScore(KEYWORD_KEY, keyword, count);
    }

    @Override
    public void decreaseAllKeywordCount() {
        Set<ZSetOperations.TypedTuple<Object>> keywords = zSetOperations.rangeWithScores(KEYWORD_KEY, 0, -1);

        if (keywords == null) return;

        keywords.forEach(keyword -> {
            if (keyword.getValue() == null || keyword.getScore() == null) return;

            double newScore = keyword.getScore() * 0.5;
            zSetOperations.add(KEYWORD_KEY, keyword.getValue(), newScore);
        });
    }

    @Override
    public List<String> getTop10Keywords() {
        Set<Object> result = zSetOperations.reverseRange(KEYWORD_KEY, 0, 9);
        if (result == null) {
            return List.of();
        }
        return result.stream().map(String::valueOf).toList();
    }
}
