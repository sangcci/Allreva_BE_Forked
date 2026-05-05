package com.backend.allreva.module.search.infra.redis;

import com.backend.allreva.module.search.application.dto.PopularKeywordResponses;
import com.backend.allreva.module.search.application.port.PopularKeywordRepository;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PopularKeywordRepositoryImpl implements PopularKeywordRepository {
    private static final String KEYWORD_KEY = "keyword";
    private static final String POPULAR_KEYWORD_KEY = "popular-keyword";

    @Resource(name = "redisTemplate")
    private ZSetOperations<String, String> zSetOperations;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, Object> valueOperations;

    @Override
    public Optional<PopularKeywordResponses> getPopularKeywordRank() {
        return Optional.ofNullable((PopularKeywordResponses) valueOperations.get(POPULAR_KEYWORD_KEY));
    }

    @Override
    public void updatePopularKeywordRank(final PopularKeywordResponses list) {
        valueOperations.set(POPULAR_KEYWORD_KEY, list);
    }

    @Override
    public void updateKeywordCount(final String keyword, final Double count) {
        zSetOperations.incrementScore(KEYWORD_KEY, keyword, count);
    }

    @Override
    public void decreaseAllKeywordCount() {
        Set<ZSetOperations.TypedTuple<String>> keywords = zSetOperations.rangeWithScores(KEYWORD_KEY, 0, -1);

        if (keywords == null) return;

        keywords.forEach(keyword -> {
            if (keyword.getValue() == null || keyword.getScore() == null) return;

            double newScore = keyword.getScore() * 0.5;
            zSetOperations.add(KEYWORD_KEY, keyword.getValue(), newScore);
        });
    }

    @Override
    public List<String> getTop10Keywords() {
        Set<String> result = zSetOperations.reverseRange(KEYWORD_KEY, 0, 9);
        if (result == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(result);
    }
}
