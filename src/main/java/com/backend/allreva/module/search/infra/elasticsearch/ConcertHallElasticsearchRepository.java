package com.backend.allreva.module.search.infra.elasticsearch;

import com.backend.allreva.module.concert.hall.domain.ConcertHallDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConcertHallElasticsearchRepository extends ElasticsearchRepository<ConcertHallDocument, String> {
}
