package com.backend.allreva.module.search.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.search.application.dto.ConcertMainResponse;
import com.backend.allreva.module.search.application.dto.ConcertSearchListResponse;
import com.backend.allreva.module.search.application.dto.ConcertThumbnail;
import com.backend.allreva.module.search.domain.ConcertDocument;
import com.backend.allreva.module.search.domain.ConcertSearchRepository;
import com.backend.allreva.module.search.domain.SortDirection;
import com.backend.allreva.module.search.exception.SearchErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertSearchService {
    private final ConcertSearchRepository concertSearchRepository;
    private final PopularKeywordService popularKeywordService;

    public List<ConcertThumbnail> searchConcertThumbnails(final String title) {
        try {
            popularKeywordService.updateKeywordCount(title);

            List<ConcertDocument> content = concertSearchRepository.findByTitleMixed(
                    title, PageRequest.of(0, 2)).getContent();
            if (content.isEmpty()) {
                throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
            }

            return content.stream()
                    .map(ConcertThumbnail::from)
                    .toList();
        } catch (CustomException e) {
            throw new CustomException(SearchErrorCode.ELASTICSEARCH_ERROR);
        }
    }

    public ConcertSearchListResponse searchConcertList(
            final String title,
            final List<Object> searchAfter,
            final int size) {
        SearchHits<ConcertDocument> searchHits = concertSearchRepository.searchByTitleList(title, searchAfter,
                size + 1);
        List<ConcertThumbnail> concertThumbnails = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(ConcertThumbnail::from)
                .limit(size)
                .toList();

        if (concertThumbnails.isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }

        boolean hasNext = searchHits.getSearchHits().size() > size;
        List<Object> nextSearchAfter = hasNext ? searchHits.getSearchHits().get(size - 1).getSortValues() : null;
        return ConcertSearchListResponse.from(concertThumbnails, nextSearchAfter);
    }

    public ConcertSearchListResponse searchAllConcertList(
            final String title,
            final List<Object> searchAfter,
            final int size) {
        SearchHits<ConcertDocument> searchHits = concertSearchRepository.searchByTitleListAll(title, searchAfter,
                size + 1);
        List<ConcertThumbnail> concertThumbnails = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(ConcertThumbnail::from)
                .limit(size)
                .toList();

        if (concertThumbnails.isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        boolean hasNext = searchHits.getSearchHits().size() > size;
        List<Object> nextSearchAfter = hasNext ? searchHits.getSearchHits().get(size - 1).getSortValues() : null;
        return ConcertSearchListResponse.from(concertThumbnails, nextSearchAfter);
    }

    public ConcertMainResponse searchMainConcerts(
            final String address,
            final List<Object> searchAfter,
            final int size,
            final SortDirection sortDirection) {

        SearchHits<ConcertDocument> searchHits = concertSearchRepository.searchMainConcerts(address, searchAfter,
                size + 1, sortDirection);
        List<ConcertThumbnail> concerts = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(ConcertThumbnail::from)
                .limit(size)
                .toList();

        if (concerts.isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }

        boolean hasNext = searchHits.getSearchHits().size() > size;
        List<Object> nextSearchAfter = hasNext ? searchHits.getSearchHits().get(size - 1).getSortValues() : null;
        return ConcertMainResponse.from(concerts, nextSearchAfter);
    }
}
