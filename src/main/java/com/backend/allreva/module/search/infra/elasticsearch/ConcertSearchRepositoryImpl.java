package com.backend.allreva.module.search.infra.elasticsearch;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.search.domain.ConcertDocument;
import com.backend.allreva.module.search.domain.ConcertSearchRepository;
import com.backend.allreva.module.search.domain.SearchField;
import com.backend.allreva.module.search.domain.SortDirection;
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
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ConcertSearchRepositoryImpl implements ConcertSearchRepository {

    private final ConcertElasticsearchRepository concertElasticsearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Page<ConcertDocument> findByTitleMixed(String title, Pageable pageable) {
        return concertElasticsearchRepository.findByTitleMixed(title, pageable);
    }

    @Override
    public Optional<ConcertDocument> findByConcertCode(String concertCode) {
        return concertElasticsearchRepository.findByConcertCode(concertCode);
    }

    @Override
    public SearchHits<ConcertDocument> searchMainConcerts(
            final String address,
            final List<Object> searchAfter,
            final int size,
            final SortDirection sortDirection) {
        try {
            NativeQuery searchQuery = getNativeQuery(SearchField.ADDRESS, address, searchAfter, size, sortDirection,
                    false);
            return elasticsearchOperations.search(searchQuery, ConcertDocument.class);
        } catch (Exception e) {
            throw new CustomException(SearchErrorCode.ELASTICSEARCH_ERROR);
        }
    }

    @Override
    public SearchHits<ConcertDocument> searchByTitleList(
            final String query,
            final List<Object> searchAfter,
            final int size) {
        return searchByTitleListInternal(query, searchAfter, size, false);
    }

    @Override
    public SearchHits<ConcertDocument> searchByTitleListAll(
            String query,
            List<Object> searchAfter,
            int size) {
        return searchByTitleListInternal(query, searchAfter, size, false);
    }

    @Override
    public ConcertDocument save(ConcertDocument document) {
        return concertElasticsearchRepository.save(document);
    }

    @Override
    public void deleteById(String id) {
        concertElasticsearchRepository.deleteById(id);
    }

    private SearchHits<ConcertDocument> searchByTitleListInternal(
            final String query,
            final List<Object> searchAfter,
            final int size,
            final boolean filterByDate) {
        try {
            NativeQuery searchQuery = getNativeQuery(SearchField.TITLE, query, searchAfter, size, SortDirection.SCORE,
                    filterByDate);
            return elasticsearchOperations.search(searchQuery, ConcertDocument.class);
        } catch (Exception e) {
            throw new CustomException(SearchErrorCode.ELASTICSEARCH_ERROR);
        }
    }

    private NativeQuery getNativeQuery(
            final SearchField searchField,
            final String searchTerm,
            final List<Object> searchAfter,
            final int size,
            final SortDirection sortDirection,
            final boolean filterByDate) {

        NativeQueryBuilder nativeQueryBuilder = NativeQuery.builder()
                .withQuery(buildSearchQuery(searchField, searchTerm, filterByDate))
                .withSort(buildPrimarySort(sortDirection))
                .withSort(buildSecondarySort())
                .withPageable(PageRequest.of(0, size));

        if (searchAfter != null && !searchAfter.isEmpty()) {
            nativeQueryBuilder.withSearchAfter(searchAfter);
        }
        return nativeQueryBuilder.build();
    }

    private Query buildSearchQuery(final SearchField searchField, final String searchTerm, final boolean filterByDate) {
        if (!StringUtils.hasText(searchTerm)) {
            return filterByDate ? buildDateFilterQuery() : Query.of(q -> q.matchAll(m -> m));
        }

        return Query.of(q -> q
                .bool(b -> {
                    BoolQuery.Builder builder = b
                            .must(m -> m
                                    .match(mt -> mt
                                            .field(searchField.getFieldName())
                                            .query(searchTerm)
                                            .fuzziness("AUTO")));
                    if (filterByDate) {
                        builder.filter(f -> f
                                .range(r -> r
                                        .field("eddate")
                                        .gte(JsonData.of("now"))));
                    }
                    return builder;
                }));
    }

    private Query buildDateFilterQuery() {
        return Query.of(q -> q
                .bool(b -> b
                        .filter(f -> f
                                .range(r -> r
                                        .field("eddate")
                                        .gte(JsonData.of("now/d"))
                                        .format("strict_date_optional_time")))));
    }

    private SortOptions buildPrimarySort(final SortDirection sortDirection) {
        return SortOptions.of(s -> s
                .field(f -> f
                        .field(getSortField(sortDirection))
                        .order(SortOrder.Desc)));
    }

    private SortOptions buildSecondarySort() {
        return SortOptions.of(s -> s
                .field(f -> f
                        .field("id")
                        .order(SortOrder.Asc)));
    }

    private String getSortField(final SortDirection sortDirection) {
        return switch (sortDirection) {
            case DATE -> "stdate";
            case VIEWS -> "view_count";
            case SCORE -> "_score";
        };
    }
}
