package com.backend.allreva.module.search.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.concert.place.application.dto.ConcertHallMainResponse;
import com.backend.allreva.module.concert.place.exception.ConcertHallErrorCode;
import com.backend.allreva.module.search.application.port.PlaceSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PlaceSearchService {

    private final PlaceSearchRepository placeSearchRepository;

    @Cacheable(
        cacheNames = "placeMain",
        key = "#address + '_' + #seatScale + '_' + #size + '_' + #cursorId",
        unless = "#result == null",
        cacheManager = "placeMainCacheManager"
    )
    public ConcertHallMainResponse searchMainPlaces(
            final String address,
            final int seatScale,
            final String cursorId,
            final int size) {
        ConcertHallMainResponse response = placeSearchRepository.searchMain(address, seatScale, cursorId, size);
        if (response.concertHallThumbnails().isEmpty()) {
            throw new CustomException(ConcertHallErrorCode.CONCERT_HALL_SEARCH_NOTFOUND);
        }
        return response;
    }
}
