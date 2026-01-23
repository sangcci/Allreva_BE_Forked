package com.backend.allreva.module.search.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.search.application.dto.SurveySearchListResponse;
import com.backend.allreva.module.search.application.dto.SurveyThumbnail;
import com.backend.allreva.module.search.domain.SurveyDocument;
import com.backend.allreva.module.search.domain.SurveySearchRepository;
import com.backend.allreva.module.search.exception.SearchErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveySearchService {
    private final SurveySearchRepository surveySearchRepository;

    public List<SurveyThumbnail> searchSurveyThumbnails(final String title) {
        try {
            List<SurveyDocument> content = surveySearchRepository.findByTitleMixed(
                    title, PageRequest.of(0, 2)).getContent();

            if (content.isEmpty()) {
                throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
            }

            return content.stream()
                    .map(SurveyThumbnail::from)
                    .toList();
        } catch (Exception e) {
            throw new CustomException(SearchErrorCode.ELASTICSEARCH_ERROR);
        }
    }

    public SurveySearchListResponse searchSurveyList(
            final String title,
            final List<Object> searchAfter,
            final int size) {
        SearchHits<SurveyDocument> searchHits = surveySearchRepository
                .searchByTitleList(title, searchAfter, size + 1);

        List<SurveyThumbnail> surveyThumbnails = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(SurveyThumbnail::from)
                .limit(size)
                .toList();

        if (surveyThumbnails.isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }

        boolean hasNext = searchHits.getSearchHits().size() > size;
        List<Object> nextSearchAfter = hasNext ? searchHits.getSearchHits().get(size - 1).getSortValues() : null;

        return SurveySearchListResponse.from(surveyThumbnails, nextSearchAfter);
    }
}
