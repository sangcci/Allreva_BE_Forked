package com.backend.allreva.module.concert.concert.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.concert.concert.application.dto.ConcertDetailResponse;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.module.concert.concert.exception.ConcertErrorCode;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import com.backend.allreva.module.concert.place.exception.ConcertHallErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final ConcertHallRepository concertHallRepository;

    @Transactional(readOnly = true)
    public ConcertDetailResponse findDetailById(final String concertCode) {
        Concert concert = concertRepository
                .findById(concertCode)
                .orElseThrow(() -> new CustomException(ConcertErrorCode.CONCERT_NOT_FOUND));
        ConcertHall hall = concertHallRepository
                .findByHallCode(concert.getHallCode())
                .orElseThrow(() -> new CustomException(ConcertHallErrorCode.CONCERT_HALL_NOT_FOUND));
        return ConcertDetailResponse.from(concert, hall);
    }
}
