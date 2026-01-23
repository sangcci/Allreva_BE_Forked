package com.backend.allreva.module.search.infra.elasticsearch;

import com.backend.allreva.module.search.domain.SurveyDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyElasticsearchRepository extends ElasticsearchRepository<SurveyDocument, String> {
    @Query("""
    {
      "bool": {
        "must": [
          {
            "match": {
              "title.mixed": {
                "query": "?0",
                "fuzziness": "AUTO"
              }
            }
          }
        ],
        "filter": [
          {
            "range": {
              "eddate": {
                "gte": "now/d"
              }
            }
          }
        ]
      }
    }
    """)
    Page<SurveyDocument> findByTitleMixed(String title, Pageable pageable);
}
