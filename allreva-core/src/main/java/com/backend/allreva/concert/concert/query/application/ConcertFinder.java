package com.backend.allreva.concert.concert.query.application;

import com.backend.allreva.common.event.Events;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.concert.concert.domain.ConcertErrorCode;
import com.backend.allreva.concert.concert.domain.SortDirection;
import com.backend.allreva.concert.concert.query.implementation.ConcertFinderPort;
import com.backend.allreva.concert.concert.query.model.ConcertDetailResult;
import com.backend.allreva.concert.concert.query.model.ConcertThumbnail;
import com.backend.allreva.concert.concert.query.model.ConcertThumbnailResult;
import com.backend.allreva.concert.concert.query.model.RelatedConcertResult;
import com.backend.allreva.concert.concert.query.model.SortDirectionView;
import com.backend.allreva.search.domain.KeywordSearchedEvent;
import com.backend.allreva.search.domain.SearchErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertFinder {

    private final ConcertFinderPort concertFinder;

    public List<ConcertThumbnailResult> getConcertSuggestions(final String title) {
        Events.raise(new KeywordSearchedEvent(title));
        List<ConcertThumbnailResult> thumbnails = concertFinder.findThumbnailsByTitle(title, 2).stream()
                .map(ConcertThumbnailResult::from)
                .toList();
        if (thumbnails.isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return thumbnails;
    }

    @Cacheable(cacheNames = "concertSearch")
    public SliceResponse<ConcertThumbnailResult, String> searchConcerts(
            final String title, final String cursorCode, final int size) {
        SliceResponse<ConcertThumbnail, String> response = concertFinder.findAllByTitle(title, cursorCode, size);
        if (response.items().isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return new SliceResponse<>(
                response.items().stream().map(ConcertThumbnailResult::from).toList(), response.nextCursor());
    }

    @Cacheable(cacheNames = "concertMain")
    public SliceResponse<ConcertThumbnailResult, String> getMainConcerts(
            final String address, final String cursorCode, final int size, final SortDirectionView sortDirection) {
        SliceResponse<ConcertThumbnail, String> response = concertFinder.findAllByAddressAndSortDirection(
                address, cursorCode, size, toSortDirection(sortDirection));
        if (response.items().isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return new SliceResponse<>(
                response.items().stream().map(ConcertThumbnailResult::from).toList(), response.nextCursor());
    }

    @Cacheable(cacheNames = "concertRelated")
    public List<RelatedConcertResult> getRelatedConcerts(
            final String hallCode, final String lastConcertCode, final int pageSize) {
        return concertFinder.findRelatedConcerts(hallCode, lastConcertCode, pageSize).stream()
                .map(RelatedConcertResult::from)
                .toList();
    }

    public ConcertDetailResult getConcertDetail(final String concertCode) {
        return concertFinder
                .findConcertDetail(concertCode)
                .map(ConcertDetailResult::from)
                .orElseThrow(() -> new CustomException(ConcertErrorCode.CONCERT_NOT_FOUND));
    }

    private SortDirection toSortDirection(final SortDirectionView sortDirection) {
        return SortDirection.valueOf(sortDirection.name());
    }
}
