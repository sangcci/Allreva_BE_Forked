package com.backend.allreva.module.search.domain;

import com.backend.allreva.module.concert.hall.domain.ConcertHallDocument;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;
import java.util.Optional;

public interface ConcertHallSearchRepository {
    SearchHits<ConcertHallDocument> searchMainConcertHall(
            String address,
            Integer minSeatSize,
            List<Object> searchAfter,
            int size
    );

    Optional<ConcertHallDocument> findById(String id);

    ConcertHallDocument save(ConcertHallDocument document);
}
