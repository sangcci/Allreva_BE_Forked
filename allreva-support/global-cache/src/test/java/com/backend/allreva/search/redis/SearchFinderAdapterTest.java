package com.backend.allreva.search.redis;

import static org.assertj.core.api.Assertions.assertThat;

import com.backend.allreva.common.config.RedisConfig;
import com.backend.allreva.search.domain.ChangeStatus;
import com.backend.allreva.search.domain.PopularKeywordChangeStatus;
import com.backend.allreva.search.domain.PopularKeywordRankItem;
import com.backend.allreva.search.domain.PopularKeywordRanks;
import com.backend.allreva.search.query.model.PopularKeywordResult;
import com.backend.allreva.support.GlobalCacheTestSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@SuppressWarnings("NonAsciiCharacters")
@ContextConfiguration(classes = {RedisConfig.class, PopularKeywordRepositoryImpl.class, SearchFinderAdapter.class})
@DisplayName("SearchFinderAdapter 테스트")
class SearchFinderAdapterTest extends GlobalCacheTestSupport {

    @Autowired
    private PopularKeywordRepositoryImpl popularKeywordRepository;

    @Autowired
    private SearchFinderAdapter searchFinderAdapter;

    @Test
    void 저장된_인기_검색어_rank를_query_result로_변환한다() {
        popularKeywordRepository.updateRank(new PopularKeywordRanks(List.of(
                PopularKeywordRankItem.builder()
                        .rank(1)
                        .keyword("아이유")
                        .changeStatus(PopularKeywordChangeStatus.UP)
                        .build(),
                PopularKeywordRankItem.builder()
                        .rank(2)
                        .keyword("데이식스")
                        .changeStatus(PopularKeywordChangeStatus.DOWN)
                        .build())));

        List<PopularKeywordResult> result = searchFinderAdapter.findPopularKeywordRank();

        assertThat(result)
                .containsExactly(
                        PopularKeywordResult.builder()
                                .rank(1)
                                .keyword("아이유")
                                .changeStatus(ChangeStatus.UP)
                                .build(),
                        PopularKeywordResult.builder()
                                .rank(2)
                                .keyword("데이식스")
                                .changeStatus(ChangeStatus.DOWN)
                                .build());
    }

    @Test
    void 저장된_rank가_없으면_빈_목록을_반환한다() {
        List<PopularKeywordResult> result = searchFinderAdapter.findPopularKeywordRank();

        assertThat(result).isEmpty();
    }
}
