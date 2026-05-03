package com.backend.allreva.module.search.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.pagination.SliceResponse;
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

    public List<RentThumbnail> getRentSuggestions(final String title) {
        List<RentThumbnail> thumbnails = rentSearchRepository.findThumbnailsByTitle(title, 2);
        if (thumbnails.isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return thumbnails;
    }

    public SliceResponse<RentThumbnail, Long> searchRents(final String title, final Long cursorId, final int size) {
        SliceResponse<RentThumbnail, Long> response = rentSearchRepository.findAllByTitle(title, cursorId, size);
        if (response.items().isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return response;
    }
}
