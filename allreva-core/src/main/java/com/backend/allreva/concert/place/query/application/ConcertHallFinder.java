package com.backend.allreva.concert.place.query.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.concert.place.domain.ConcertHallErrorCode;
import com.backend.allreva.concert.place.query.implementation.ConcertHallFinderPort;
import com.backend.allreva.concert.place.query.model.ConcertHallDetailResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConcertHallFinder {

    private final ConcertHallFinderPort concertHallFinderPort;

    @Cacheable(cacheNames = "concertHall", key = "#hallCode")
    @Transactional(readOnly = true)
    public ConcertHallDetailResult getConcertHallDetail(final String hallCode) {
        return concertHallFinderPort
                .findConcertHallDetail(hallCode)
                .orElseThrow(() -> new CustomException(ConcertHallErrorCode.CONCERT_HALL_NOT_FOUND));
    }
}
