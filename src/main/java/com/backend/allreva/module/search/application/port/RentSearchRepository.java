package com.backend.allreva.module.search.application.port;

import com.backend.allreva.module.search.application.dto.RentSearchListResponse;
import com.backend.allreva.module.search.application.dto.RentThumbnail;

import java.util.List;

public interface RentSearchRepository {

    List<RentThumbnail> findThumbnailsByTitle(String title, int limit);

    RentSearchListResponse searchByTitle(String query, Long cursorId, int pageSize);
}
