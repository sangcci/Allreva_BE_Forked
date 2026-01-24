package com.backend.allreva.module.search.infra.elasticsearch;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.concert.hall.domain.ConcertHallDocument;
import com.backend.allreva.module.search.domain.ConcertHallSearchRepository;
import com.backend.allreva.module.search.exception.SearchErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
public class ConcertHallSearchRepositoryImpl implements ConcertHallSearchRepository {

    private final ConcertHallElasticsearchRepository concertHallElasticsearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public SearchHits<ConcertHallDocument> searchMainConcertHall(
            final String address,
            final Integer minSeatSize,
            final List<Object> searchAfter,
            final int size) {
        try {
            NativeQuery searchQuery = getNativeQuery(address, minSeatSize, searchAfter, size);
            return elasticsearchOperations.search(searchQuery, ConcertHallDocument.class);
        } catch (Exception e) {
            throw new CustomException(SearchErrorCode.ELASTICSEARCH_ERROR);
        }
    }

    @Override
    public Optional<ConcertHallDocument> findById(String id) {
        return concertHallElasticsearchRepository.findById(id);
    }

    @Override
    public ConcertHallDocument save(ConcertHallDocument document) {
        return concertHallElasticsearchRepository.save(document);
    }

    private NativeQuery getNativeQuery(
            final String address,
            final Integer minSeatSize,
            final List<Object> searchAfter,
            final int size) {

        NativeQueryBuilder nativeQueryBuilder = NativeQuery.builder()
                .withQuery(buildSearchQuery(address, minSeatSize))
                .withPageable(PageRequest.of(0, size));

        // 정렬 조건 추가
        if (minSeatSize != 0) {
            // seatScale이 있는 경우 우선 seatScale로 정렬
            nativeQueryBuilder.withSort(SortOptions.of(s -> s
                    .field(f -> f
                            .field("seat_scale")
                            .order(SortOrder.Desc))));
        }

        if (StringUtils.hasText(address)) {
            // address가 있는 경우 _score로 정렬
            nativeQueryBuilder.withSort(SortOptions.of(s -> s
                    .score(sc -> sc
                            .order(SortOrder.Desc))));
        }

        // 마지막으로 항상 id로 정렬
        nativeQueryBuilder.withSort(SortOptions.of(s -> s
                .field(f -> f
                        .field("id")
                        .order(SortOrder.Asc))));

        if (searchAfter != null && !searchAfter.isEmpty()) {
            nativeQueryBuilder.withSearchAfter(searchAfter);
        }
        return nativeQueryBuilder.build();
    }

    private Query buildSearchQuery(final String address, final Integer minSeatSize) {
        if (!StringUtils.hasText(address)) {
            return Query.of(q -> q.matchAll(m -> m));
        }
        return Query.of(q -> q
                .bool(b -> {
                    BoolQuery.Builder builder = b
                            .must(m -> m
                                    .match(mt -> mt
                                            .field("address.mixed")
                                            .query(address)
                                            .fuzziness("AUTO")));

                    if (minSeatSize != 0) {
                        builder.filter(f -> f
                                .range(r -> r
                                        .field("seat_scale")
                                        .gte(JsonData.of(minSeatSize))));
                    }

                    return builder;
                }));
    }
}
