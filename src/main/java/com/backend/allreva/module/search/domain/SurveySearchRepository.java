package com.backend.allreva.module.search.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;
import java.util.Optional;

public interface SurveySearchRepository {
    Page<SurveyDocument> findByTitleMixed(String title, Pageable pageable);

    SearchHits<SurveyDocument> searchByTitleList(
            String query,
            List<Object> searchAfter,
            int size
    );

    Optional<SurveyDocument> findById(String id);

    SurveyDocument save(SurveyDocument document);

    void deleteById(String id);
}
