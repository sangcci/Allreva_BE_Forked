package com.backend.allreva.module.concert.concert.application;

import com.backend.allreva.common.event.Events;
import com.backend.allreva.common.event.KeywordSearchedEvent;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.module.concert.concert.application.dto.ConcertDetailResponse;
import com.backend.allreva.module.concert.concert.application.dto.ConcertThumbnail;
import com.backend.allreva.module.concert.concert.application.dto.RelatedConcertResponse;
import com.backend.allreva.module.concert.concert.application.dto.SortDirection;
import com.backend.allreva.module.concert.concert.application.port.ConcertSearchRepository;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.module.concert.concert.exception.ConcertErrorCode;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import com.backend.allreva.module.search.exception.SearchErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final ConcertHallRepository concertHallRepository;
    private final ConcertSearchRepository concertSearchRepository;

    @Transactional(readOnly = true)
    public List<ConcertThumbnail> getConcertSuggestions(final String title) {
        Events.raise(new KeywordSearchedEvent(title));
        List<ConcertThumbnail> thumbnails = concertSearchRepository.findThumbnailsByTitle(title, 2);
        if (thumbnails.isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return thumbnails;
    }

    @Cacheable(cacheNames = "concertSearch")
    @Transactional(readOnly = true)
    public SliceResponse<ConcertThumbnail, String> searchConcerts(
            final String title, final String cursorCode, final int size) {
        SliceResponse<ConcertThumbnail, String> response =
                concertSearchRepository.findAllByTitle(title, cursorCode, size);
        if (response.items().isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return response;
    }

    @Cacheable(cacheNames = "concertMain")
    @Transactional(readOnly = true)
    public SliceResponse<ConcertThumbnail, String> getMainConcerts(
            final String address, final String cursorCode, final int size, final SortDirection sortDirection) {
        SliceResponse<ConcertThumbnail, String> response =
                concertSearchRepository.findAllByAddressAndSortDirection(address, cursorCode, size, sortDirection);
        if (response.items().isEmpty()) {
            throw new CustomException(SearchErrorCode.SEARCH_RESULT_NOT_FOUND);
        }
        return response;
    }

    @Cacheable(cacheNames = "concertRelated")
    @Transactional(readOnly = true)
    public List<RelatedConcertResponse> getRelatedConcerts(
            final String hallCode, final String lastConcertCode, final int pageSize) {
        return concertRepository.findAllByHallCode(hallCode, lastConcertCode, pageSize).stream()
                .map(RelatedConcertResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConcertDetailResponse getConcertDetail(final String concertCode) {
        Concert concert = concertRepository
                .findById(concertCode)
                .orElseThrow(() -> new CustomException(ConcertErrorCode.CONCERT_NOT_FOUND));
        ConcertHall hall = concert != null
                ? concertHallRepository.findById(concert.getHallCode()).orElse(null)
                : null;
        return ConcertDetailResponse.from(concert, hall);
    }
}
