package com.backend.allreva.concert.query.application;

import java.util.List;

import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.concert.command.domain.ConcertRepository;
import com.backend.allreva.concert.exception.ConcertErrorCode;
import com.backend.allreva.concert.infra.elasticsearch.ConcertDocument;
import com.backend.allreva.concert.infra.elasticsearch.ConcertSearchRepository;
import com.backend.allreva.concert.infra.elasticsearch.SortDirection;
import com.backend.allreva.concert.query.application.response.ConcertDetailResponse;
import com.backend.allreva.concert.query.application.response.ConcertMainResponse;
import com.backend.allreva.concert.query.application.response.ConcertThumbnail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class ConcertQueryService {

    private final ConcertRepository concertRepository;
    private final ConcertSearchRepository concertSearchRepository;

    public ConcertDetailResponse findDetailById(final Long concertId) {
        concertRepository.increaseViewCount(concertId);
        return concertRepository.findDetailById(concertId);
    }

    public ConcertMainResponse getConcertMain(
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
            throw new CustomException(ConcertErrorCode.CONCERT_SEARCH_NOT_FOUND);
        }

        boolean hasNext = searchHits.getSearchHits().size() > size;
        List<Object> nextSearchAfter = hasNext ? searchHits.getSearchHits().get(size - 1).getSortValues() : null;
        return ConcertMainResponse.from(concerts, nextSearchAfter);

    }

    public List<ConcertThumbnail> getConcertMainList() {
        return concertRepository.getConcertMainThumbnails();
    }

}
