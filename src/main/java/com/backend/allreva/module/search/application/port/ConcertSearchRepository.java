package com.backend.allreva.module.search.application.port;

import com.backend.allreva.module.search.application.dto.ConcertMainResponse;
import com.backend.allreva.module.search.application.dto.ConcertSearchListResponse;
import com.backend.allreva.module.search.application.dto.ConcertThumbnail;
import com.backend.allreva.module.search.application.dto.SortDirection;
import java.util.List;

public interface ConcertSearchRepository {

    List<ConcertThumbnail> findThumbnailsByTitle(String title, int limit);

    ConcertSearchListResponse searchByTitle(String query, String cursorCode, int pageSize);

    ConcertSearchListResponse searchByTitleAll(String query, String cursorCode, int pageSize);

    ConcertMainResponse searchMain(String address, String cursorCode, int pageSize, SortDirection sortDirection);
}
