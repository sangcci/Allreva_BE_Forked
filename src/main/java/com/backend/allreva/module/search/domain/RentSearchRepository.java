package com.backend.allreva.module.search.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;

public interface RentSearchRepository {
    Page<RentDocument> findByTitleMixed(String title, Pageable pageable);

    SearchHits<RentDocument> searchByTitleList(
            String query,
            List<Object> searchAfter,
            int size
    );

    RentDocument save(RentDocument document);

    void deleteById(String id);
}
