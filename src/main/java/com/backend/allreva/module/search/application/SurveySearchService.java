package com.backend.allreva.module.search.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.search.application.dto.SurveySearchListResponse;
import com.backend.allreva.module.search.application.dto.SurveyThumbnail;
import com.backend.allreva.module.search.application.port.SurveySearchRepository;
import com.backend.allreva.module.search.exception.SearchErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveySearchService {
    private final SurveySearchRepository surveySearchRepository;

    public List<SurveyThumbnail> searchSurveyThumbnails(final String title) {
        List<SurveyThumbnail> thumbnails = surveySearchRepository.findThumbnailsByTitle(title, 2);
        if (thumbnails.isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return thumbnails;
    }

    public SurveySearchListResponse searchSurveyList(
            final String title, final Long cursorId, final int size) {
        SurveySearchListResponse response = surveySearchRepository.searchByTitle(title, cursorId, size);
        if (response.surveyThumbnails().isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return response;
    }
}
