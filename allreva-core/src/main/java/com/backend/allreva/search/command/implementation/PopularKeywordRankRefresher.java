package com.backend.allreva.search.command.implementation;

import com.backend.allreva.search.domain.PopularKeywordRanks;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopularKeywordRankRefresher {

    private final PopularKeywordRankReader popularKeywordRankReader;
    private final PopularKeywordRankCalculator popularKeywordRankCalculator;
    private final PopularKeywordRankWriter popularKeywordRankWriter;

    public void update() {
        List<String> currentKeywords = popularKeywordRankReader.currentKeywords();
        List<String> topKeywords = popularKeywordRankReader.topKeywords();
        PopularKeywordRanks ranks = popularKeywordRankCalculator.calculate(currentKeywords, topKeywords);
        popularKeywordRankWriter.update(ranks);
    }
}
