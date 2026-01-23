package com.backend.allreva.module.search.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;
import java.util.Optional;

public interface ConcertSearchRepository {
    Page<ConcertDocument> findByTitleMixed(String title, Pageable pageable);

    Optional<ConcertDocument> findByConcertCode(String concertCode);

    SearchHits<ConcertDocument> searchMainConcerts(
            String address,
            List<Object> searchAfter,
            int size,
            SortDirection sortDirection
    );

    SearchHits<ConcertDocument> searchByTitleList(
            String query,
            List<Object> searchAfter,
            int size
    );

    SearchHits<ConcertDocument> searchByTitleListAll(
            String query,
            List<Object> searchAfter,
            int size
    );

    ConcertDocument save(ConcertDocument document);

    void deleteById(String id);
}
