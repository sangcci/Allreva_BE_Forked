package com.backend.allreva.module.search.application.port;

import com.backend.allreva.common.web.response.SliceResponse;
import com.backend.allreva.module.search.application.dto.SurveyThumbnail;
import java.util.List;

public interface SurveySearchRepository {

    List<SurveyThumbnail> findThumbnailsByTitle(String title, int limit);

    SliceResponse<SurveyThumbnail, Long> findAllByTitle(String query, Long cursorId, int pageSize);
}
