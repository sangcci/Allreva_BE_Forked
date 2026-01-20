package com.backend.allreva.survey.query.application;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import com.backend.allreva.common.exception.CustomException;

import com.backend.allreva.concert.exception.search.SearchErrorCode;
import com.backend.allreva.survey.infra.elasticsearch.SurveyDocument;
import com.backend.allreva.survey.infra.elasticsearch.SurveyDocumentRepository;
import com.backend.allreva.survey.query.application.response.SurveySearchListResponse;
import com.backend.allreva.survey.query.application.response.SurveyThumbnail;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveySearchService {
    private final SurveyDocumentRepository surveyDocumentRepository;

    public List<SurveyThumbnail> searchSurveyThumbnails(final String title) {
        try {
            List<SurveyDocument> content = surveyDocumentRepository.findByTitleMixed(
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
        SearchHits<SurveyDocument> searchHits = surveyDocumentRepository
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
