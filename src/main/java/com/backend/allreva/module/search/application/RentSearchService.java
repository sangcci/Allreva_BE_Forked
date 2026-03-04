package com.backend.allreva.module.search.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.search.application.dto.RentSearchListResponse;
import com.backend.allreva.module.search.application.dto.RentThumbnail;
import com.backend.allreva.module.search.application.port.RentSearchRepository;
import com.backend.allreva.module.search.exception.SearchErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentSearchService {
    private final RentSearchRepository rentSearchRepository;

    public List<RentThumbnail> searchRentThumbnails(final String title) {
        List<RentThumbnail> thumbnails = rentSearchRepository.findThumbnailsByTitle(title, 2);
        if (thumbnails.isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return thumbnails;
    }

    public RentSearchListResponse searchRentSearchList(final String title, final Long cursorId, final int size) {
        RentSearchListResponse response = rentSearchRepository.searchByTitle(title, cursorId, size);
        if (response.rentThumbnails().isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return response;
    }
}
