package com.backend.allreva.module.search.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.web.response.SliceResponse;
import com.backend.allreva.module.search.application.dto.ConcertThumbnail;
import com.backend.allreva.module.search.application.dto.SortDirection;
import com.backend.allreva.module.search.application.port.ConcertSearchRepository;
import com.backend.allreva.module.search.exception.SearchErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertSearchService {
    private final ConcertSearchRepository concertSearchRepository;
    private final PopularKeywordService popularKeywordService;

    public List<ConcertThumbnail> getConcertSuggestions(final String title) {
        popularKeywordService.updateKeywordCount(title);
        List<ConcertThumbnail> thumbnails = concertSearchRepository.findThumbnailsByTitle(title, 2);
        if (thumbnails.isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return thumbnails;
    }

    public SliceResponse<ConcertThumbnail, String> searchConcerts(
            final String title, final String cursorCode, final int size) {
        SliceResponse<ConcertThumbnail, String> response =
                concertSearchRepository.findAllByTitle(title, cursorCode, size);
        if (response.items().isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return response;
    }

    public SliceResponse<ConcertThumbnail, String> getMainConcerts(
            final String address, final String cursorCode, final int size, final SortDirection sortDirection) {
        SliceResponse<ConcertThumbnail, String> response =
                concertSearchRepository.findAllByAddressAndSortDirection(address, cursorCode, size, sortDirection);
        if (response.items().isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return response;
    }
}
