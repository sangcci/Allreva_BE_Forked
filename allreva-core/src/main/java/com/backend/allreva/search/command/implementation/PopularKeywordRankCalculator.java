package com.backend.allreva.search.command.implementation;

import com.backend.allreva.search.domain.PopularKeywordChangeStatus;
import com.backend.allreva.search.domain.PopularKeywordRankItem;
import com.backend.allreva.search.domain.PopularKeywordRanks;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PopularKeywordRankCalculator {

    private static final int NOT_EXIST_RANK = -1;

    public PopularKeywordRanks calculate(final List<String> currentKeywords, final List<String> updatedKeywords) {
        List<PopularKeywordRankItem> items = updatedKeywords.stream()
                .map(keyword -> rankItem(keyword, currentKeywords, updatedKeywords))
                .toList();
        return new PopularKeywordRanks(items);
    }

    private PopularKeywordRankItem rankItem(
            final String keyword, final List<String> currentKeywords, final List<String> updatedKeywords) {
        int oldRank = currentKeywords.indexOf(keyword);
        int newRank = updatedKeywords.indexOf(keyword);
        return PopularKeywordRankItem.builder()
                .rank(newRank + 1)
                .keyword(keyword)
                .changeStatus(changeStatus(oldRank, newRank))
                .build();
    }

    private PopularKeywordChangeStatus changeStatus(final int oldRank, final int newRank) {
        if (oldRank == NOT_EXIST_RANK) {
            return PopularKeywordChangeStatus.UP;
        }
        if (newRank < oldRank) {
            return PopularKeywordChangeStatus.UP;
        }
        if (newRank > oldRank) {
            return PopularKeywordChangeStatus.DOWN;
        }
        return PopularKeywordChangeStatus.STAY;
    }
}
