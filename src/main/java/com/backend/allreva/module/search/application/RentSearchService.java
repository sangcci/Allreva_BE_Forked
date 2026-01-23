package com.backend.allreva.module.search.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.search.application.dto.RentSearchListResponse;
import com.backend.allreva.module.search.application.dto.RentThumbnail;
import com.backend.allreva.module.search.domain.RentDocument;
import com.backend.allreva.module.search.domain.RentSearchRepository;
import com.backend.allreva.module.search.exception.SearchErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RentSearchService {
    private final RentSearchRepository rentSearchRepository;

    public List<RentThumbnail> searchRentThumbnails(String title) {
        try {
            List<RentDocument> content = rentSearchRepository.findByTitleMixed(
                    title, PageRequest.of(0, 2)).getContent();

            if (content.isEmpty()) {
                throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
            }
            return content.stream()
                    .map(RentThumbnail::from)
                    .toList();
        } catch (Exception e) {
            throw new CustomException(SearchErrorCode.ELASTICSEARCH_ERROR);
        }
    }

    public RentSearchListResponse searchRentSearchList(
            final String title,
            final List<Object> searchAfter,
            final int size) {
        SearchHits<RentDocument> searchHits = rentSearchRepository
                .searchByTitleList(title, searchAfter, size + 1);

        List<RentThumbnail> rentThumbnails = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(RentThumbnail::from)
                .limit(size)
                .toList();

        if (rentThumbnails.isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }

        boolean hasNext = searchHits.getSearchHits().size() > size;
        List<Object> nextSearchAfter = hasNext ? searchHits.getSearchHits().get(size - 1).getSortValues() : null;

        return RentSearchListResponse.from(rentThumbnails, nextSearchAfter);
    }
}
