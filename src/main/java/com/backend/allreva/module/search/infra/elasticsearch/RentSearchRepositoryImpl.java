package com.backend.allreva.module.search.infra.elasticsearch;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.search.domain.RentDocument;
import com.backend.allreva.module.search.domain.RentSearchRepository;
import com.backend.allreva.module.search.exception.SearchErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RentSearchRepositoryImpl implements RentSearchRepository {

    private final RentElasticsearchRepository rentElasticsearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Page<RentDocument> findByTitleMixed(String title, Pageable pageable) {
        return rentElasticsearchRepository.findByTitleMixed(title, pageable);
    }

    @Override
    public SearchHits<RentDocument> searchByTitleList(
            final String query,
            final List<Object> searchAfter,
            final int size) {
        try {
            NativeQuery nativeQuery = getNativeQuery(query, searchAfter, size);
            return elasticsearchOperations.search(nativeQuery, RentDocument.class);
        } catch (Exception e) {
            throw new CustomException(SearchErrorCode.ELASTICSEARCH_ERROR);
        }
    }

    @Override
    public RentDocument save(RentDocument document) {
        return rentElasticsearchRepository.save(document);
    }

    @Override
    public void deleteById(String id) {
        rentElasticsearchRepository.deleteById(id);
    }

    private NativeQuery getNativeQuery(
            final String searchTerm,
            final List<Object> searchAfter,
            final int size) {
        NativeQueryBuilder nativeQueryBuilder = NativeQuery.builder()
                .withQuery(buildSearchQuery(searchTerm))
                .withSort(buildPrimarySort())
                .withSort(buildSecondarySort())
                .withPageable(PageRequest.of(0, size));

        if (searchAfter != null && !searchAfter.isEmpty()) {
            nativeQueryBuilder.withSearchAfter(searchAfter);
        }
        return nativeQueryBuilder.build();
    }

    private Query buildSearchQuery(final String searchTerm) {
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();

        boolQuery.filter(f -> f
                .range(r -> r
                        .field("eddate")
                        .gte(JsonData.of("now/d"))
                        .format("strict_date_optional_time")));

        if (StringUtils.hasText(searchTerm)) {
            boolQuery.must(m -> m
                    .match(mt -> mt
                            .field("title.mixed")
                            .query(searchTerm)
                            .fuzziness("AUTO")));
        } else {
            boolQuery.must(m -> m
                    .matchAll(ma -> ma));
        }

        return Query.of(q -> q.bool(boolQuery.build()));
    }

    private SortOptions buildPrimarySort() {
        return SortOptions.of(s -> s
                .score(f -> f
                        .order(SortOrder.Desc)));
    }

    private SortOptions buildSecondarySort() {
        return SortOptions.of(s -> s
                .field(f -> f
                        .field("id")
                        .order(SortOrder.Asc)));
    }
}
