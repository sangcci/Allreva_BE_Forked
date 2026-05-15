package com.backend.allreva.module.recruitment.survey.application.query.port;

import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.module.recruitment.survey.application.dto.SurveyThumbnail;
import java.util.List;

public interface SurveySearchRepository {

    List<SurveyThumbnail> findThumbnailsByTitle(String title, int limit);

    SliceResponse<SurveyThumbnail, Long> findAllByTitle(String query, Long cursorId, int pageSize);
}
