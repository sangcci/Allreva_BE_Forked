package com.backend.allreva.module.search.application;

import com.backend.allreva.module.search.application.dto.ChangeStatus;
import com.backend.allreva.module.search.application.dto.PopularKeywordResponse;
import com.backend.allreva.module.search.application.dto.PopularKeywordResponses;
import com.backend.allreva.module.search.application.port.PopularKeywordRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PopularKeywordService {
    private final PopularKeywordRepository popularKeywordRepository;
    public static final Integer NOT_EXIST_RANK = -1;

    public void updateKeywordCount(final String keyword) {
        popularKeywordRepository.updateKeywordCount(keyword, 1.0);
    }

    public void decreaseAllKeywordCount() {
        popularKeywordRepository.decreaseAllKeywordCount();
    }

    public void updatePopularKeywordRank() {
        List<String> top10 = popularKeywordRepository
                .getPopularKeywordRank()
                .map(PopularKeywordResponses::popularKeywordResponses)
                .orElse(Collections.emptyList())
                .stream()
                .map(PopularKeywordResponse::keyword)
                .toList();

        List<String> updatedTop10 = popularKeywordRepository.getTop10Keywords();

        compareAndSaveRankChanges(top10, updatedTop10);
    }

    public void compareAndSaveRankChanges(final List<String> top10, final List<String> updatedTop10) {
        List<PopularKeywordResponse> list = new ArrayList<>();
        for (int i = 0; i < updatedTop10.size(); i++) {
            String keyword = updatedTop10.get(i);
            int rank = top10.indexOf(keyword);

            ChangeStatus status = getChangeStatus(rank, i);

            list.add(PopularKeywordResponse.builder()
                    .rank(i + 1)
                    .keyword(keyword)
                    .changeStatus(status)
                    .build());
        }

        popularKeywordRepository.updatePopularKeywordRank(new PopularKeywordResponses(list));
    }

    private ChangeStatus getChangeStatus(int oldRank, int rank) {
        if (oldRank == NOT_EXIST_RANK) {
            return ChangeStatus.UP;
        }
        if (rank < oldRank) {
            return ChangeStatus.UP;
        }
        if (rank > oldRank) {
            return ChangeStatus.DOWN;
        }
        return ChangeStatus.STAY;
    }

    public List<PopularKeywordResponse> getPopularKeywordRank() {
        return popularKeywordRepository
                .getPopularKeywordRank()
                .map(PopularKeywordResponses::popularKeywordResponses)
                .orElse(Collections.emptyList());
    }
}
