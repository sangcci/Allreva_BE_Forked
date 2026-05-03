package com.backend.allreva.module.search.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.module.search.application.dto.SurveyThumbnail;
import com.backend.allreva.module.search.application.port.SurveySearchRepository;
import com.backend.allreva.module.search.exception.SearchErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SurveySearchService {
    private final SurveySearchRepository surveySearchRepository;

    public List<SurveyThumbnail> getSurveySuggestions(final String title) {
        List<SurveyThumbnail> thumbnails = surveySearchRepository.findThumbnailsByTitle(title, 2);
        if (thumbnails.isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return thumbnails;
    }

    public SliceResponse<SurveyThumbnail, Long> searchSurveys(final String title, final Long cursorId, final int size) {
        SliceResponse<SurveyThumbnail, Long> response = surveySearchRepository.findAllByTitle(title, cursorId, size);
        if (response.items().isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return response;
    }
}
