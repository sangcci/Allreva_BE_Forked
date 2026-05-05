package com.backend.allreva.module.concert.place.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.concert.place.application.dto.ConcertHallDetailResponse;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import com.backend.allreva.module.concert.place.exception.ConcertHallErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConcertHallService {

    private final ConcertHallRepository concertHallRepository;

    @Cacheable(cacheNames = "concertHall", key = "#hallCode")
    @Transactional(readOnly = true)
    public ConcertHallDetailResponse getConcertHallDetail(final String hallCode) {
        ConcertHall hall = concertHallRepository
                .findById(hallCode)
                .orElseThrow(() -> new CustomException(ConcertHallErrorCode.CONCERT_HALL_NOT_FOUND));
        return ConcertHallDetailResponse.from(hall);
    }
}
