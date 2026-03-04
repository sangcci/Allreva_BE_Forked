package com.backend.allreva.module.search.application.port;

import com.backend.allreva.module.search.application.dto.SurveySearchListResponse;
import com.backend.allreva.module.search.application.dto.SurveyThumbnail;
import java.util.List;

public interface SurveySearchRepository {

    List<SurveyThumbnail> findThumbnailsByTitle(String title, int limit);

    SurveySearchListResponse searchByTitle(String query, Long cursorId, int pageSize);
}
