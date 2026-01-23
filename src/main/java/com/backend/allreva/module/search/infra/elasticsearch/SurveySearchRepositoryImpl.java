package com.backend.allreva.module.search.infra.elasticsearch;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.search.domain.SurveyDocument;
import com.backend.allreva.module.search.domain.SurveySearchRepository;
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
public class SurveySearchRepositoryImpl implements SurveySearchRepository {

    private final SurveyElasticsearchRepository surveyElasticsearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Page<SurveyDocument> findByTitleMixed(String title, Pageable pageable) {
        return surveyElasticsearchRepository.findByTitleMixed(title, pageable);
    }

    @Override
    public SearchHits<SurveyDocument> searchByTitleList(
            final String query,
            final List<Object> searchAfter,
            final int size) {
        try {
            NativeQuery nativeQuery = getNativeQuery(query, searchAfter, size);
            return elasticsearchOperations.search(nativeQuery, SurveyDocument.class);
        } catch (Exception e) {
            throw new CustomException(SearchErrorCode.ELASTICSEARCH_ERROR);
        }
    }

    @Override
    public Optional<SurveyDocument> findById(String id) {
        return surveyElasticsearchRepository.findById(id);
    }

    @Override
    public SurveyDocument save(SurveyDocument document) {
        return surveyElasticsearchRepository.save(document);
    }

    @Override
    public void deleteById(String id) {
        surveyElasticsearchRepository.deleteById(id);
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
