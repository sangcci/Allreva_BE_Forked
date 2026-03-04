package com.backend.allreva.module.search.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.search.application.dto.ConcertMainResponse;
import com.backend.allreva.module.search.application.dto.ConcertSearchListResponse;
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

    public List<ConcertThumbnail> searchConcertThumbnails(final String title) {
        popularKeywordService.updateKeywordCount(title);
        List<ConcertThumbnail> thumbnails = concertSearchRepository.findThumbnailsByTitle(title, 2);
        if (thumbnails.isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return thumbnails;
    }

    public ConcertSearchListResponse searchConcertList(final String title, final Long cursorId, final int size) {
        ConcertSearchListResponse response = concertSearchRepository.searchByTitle(title, cursorId, size);
        if (response.concertThumbnails().isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return response;
    }

    public ConcertSearchListResponse searchAllConcertList(final String title, final Long cursorId, final int size) {
        ConcertSearchListResponse response = concertSearchRepository.searchByTitleAll(title, cursorId, size);
        if (response.concertThumbnails().isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return response;
    }

    public ConcertMainResponse searchMainConcerts(
            final String address, final Long cursorId, final int size, final SortDirection sortDirection) {
        ConcertMainResponse response = concertSearchRepository.searchMain(address, cursorId, size, sortDirection);
        if (response.concertThumbnails().isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return response;
    }
}
