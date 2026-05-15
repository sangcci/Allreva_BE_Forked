package com.backend.allreva.module.recruitment.rent.application.query.port;

import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.module.recruitment.rent.application.query.dto.RentThumbnail;
import java.util.List;

public interface RentSearchRepository {

    List<RentThumbnail> findThumbnailsByTitle(String title, int limit);

    SliceResponse<RentThumbnail, Long> findAllByTitle(String query, Long cursorId, int pageSize);
}
