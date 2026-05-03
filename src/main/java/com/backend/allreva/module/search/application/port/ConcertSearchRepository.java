package com.backend.allreva.module.search.application.port;

import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.module.search.application.dto.ConcertThumbnail;
import com.backend.allreva.module.search.application.dto.SortDirection;
import java.util.List;

public interface ConcertSearchRepository {

    List<ConcertThumbnail> findThumbnailsByTitle(String title, int limit);

    SliceResponse<ConcertThumbnail, String> findAllByTitle(String query, String cursorCode, int pageSize);

    SliceResponse<ConcertThumbnail, String> findAllByAddressAndSortDirection(
            String address, String cursorCode, int pageSize, SortDirection sortDirection);
}
