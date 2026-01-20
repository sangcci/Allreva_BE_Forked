package com.backend.allreva.rent.query.application;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import com.backend.allreva.common.exception.CustomException;

import com.backend.allreva.concert.exception.search.SearchErrorCode;
import com.backend.allreva.rent.infra.elasticsearch.RentDocument;
import com.backend.allreva.rent.infra.elasticsearch.RentDocumentRepository;
import com.backend.allreva.rent.query.application.response.RentSearchListResponse;
import com.backend.allreva.rent.query.application.response.RentThumbnail;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentSearchService {
    private final RentDocumentRepository rentDocumentRepository;

    public List<RentThumbnail> searchRentThumbnails(String title) {
        try {
            List<RentDocument> content = rentDocumentRepository.findByTitleMixed(
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
        SearchHits<RentDocument> searchHits = rentDocumentRepository
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
