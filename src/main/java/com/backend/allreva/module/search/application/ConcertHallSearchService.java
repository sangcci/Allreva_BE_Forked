package com.backend.allreva.module.search.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.concert.hall.application.dto.ConcertHallMainResponse;
import com.backend.allreva.module.concert.hall.exception.ConcertHallErrorCode;
import com.backend.allreva.module.search.domain.ConcertHallSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ConcertHallSearchService {

    private final ConcertHallSearchRepository concertHallSearchRepository;

    @Cacheable(
        cacheNames = "concertHallMain",
        key = "#address + '_' + #seatScale + '_' + #size + '_' + #cursorId",
        unless = "#result == null",
        cacheManager = "concertHallMainCacheManager"
    )
    public ConcertHallMainResponse searchMainConcertHalls(
            final String address,
            final int seatScale,
            final String cursorId,
            final int size) {
        ConcertHallMainResponse response = concertHallSearchRepository.searchMain(address, seatScale, cursorId, size);
        if (response.concertHallThumbnails().isEmpty()) {
            throw new CustomException(ConcertHallErrorCode.CONCERT_HALL_SEARCH_NOTFOUND);
        }
        return response;
    }
}
